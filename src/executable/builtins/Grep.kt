package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.executable.Command
import com.boris.bash.executable.complain
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import org.apache.commons.io.input.CloseShieldReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader
import java.io.Writer
import java.nio.file.Path

internal class Grep : Command, CliktCommand() {
    companion object Builder : Command.Builder<Grep> {
        override val commandName = "grep"

        override fun getCommand(arguments: List<String>): Grep {
            return Grep().apply {
                main(arguments)
                val options = when {
                    ignoreCase -> setOf(RegexOption.IGNORE_CASE)
                    else -> emptySet()
                }
                regex = when {
                    completeWordOnly -> Regex("\\b$pattern\\b", options)
                    else -> Regex(pattern, options)
                }
            }
        }
    }

    override val name = commandName

    private val ignoreCase: Boolean by option(
        "-i", "--ignore-case", help = "Perform case insensitive matching.  By default, grep is case sensitive."
    ).flag(default = false)

    private val completeWordOnly: Boolean by option(
        "-w", "--word-regexp", help = "The expression is searched for as a word."
    ).flag(default = false)

    private val nFollowingLines: Int by option(
        "-A", "--after-context", help = "Print num lines of trailing context after each match.", metavar = "num"
    ).int().default(0)

    private val pattern: String by argument("pattern")
    private lateinit var regex: Regex

    private val paths: List<Path> by argument("file").path().multiple()

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        var code = 0

        if (paths.isEmpty()) {
            grep(CloseShieldReader.wrap(input), output)
        } else {
            for ((i, path) in paths.withIndex()) {
                val fileReader = try {
                    FileReader(path.toFile())
                } catch (e: FileNotFoundException) {
                    code = 1
                    complain(err, path.toString(), "No such file or directory")
                    continue
                }
                val prefix = when {
                    paths.size > 1 -> "$path:"
                    else -> ""
                }
                fileReader.use {
                    grep(it, output, prefix = prefix, mayPrintSeparator = i > 0)
                }
            }
        }

        return code
    }

    private fun grep(input: Reader, output: Writer, prefix: String = "", mayPrintSeparator: Boolean = false) {
        var notFirstBlock = false
        var linesToPrint = 0

        input.forEachLine {
            val regexSatisfaction = regex.containsMatchIn(it)

            if (regexSatisfaction) {
                if (linesToPrint == 0 && nFollowingLines > 0 && mayPrintSeparator) {
                    if (notFirstBlock) {
                        output.appendLine("--")
                    } else {
                        notFirstBlock = true
                    }
                }
                output.appendLine("$prefix$it")
                linesToPrint = nFollowingLines
            } else if (linesToPrint > 0) {
                output.appendLine("$prefix$it")
                linesToPrint--
            }
            output.flush()
        }
    }

    // Used only as a part of the option-parsing machinery
    override fun run() = Unit
}
