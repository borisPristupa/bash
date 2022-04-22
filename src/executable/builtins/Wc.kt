package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.executable.Command
import com.boris.bash.executable.complain
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader
import java.io.Writer

internal class Wc(private val arguments: List<String>) : Command {
    companion object Builder : Command.Builder<Wc> {
        private val WORD_REGEX = Regex("\\S+")

        override val commandName = "wc"

        override fun getCommand(arguments: List<String>): Wc {
            return Wc(arguments)
        }
    }

    override val name: String = commandName

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        if (arguments.isEmpty()) {
            val text = input.buffered().readText().replace(System.lineSeparator(), "\n")
            output.printCounts(wc(text), null)
            return 0
        }

        var code = 0
        var total = Counts(0, 0, 0)

        for (fileName in arguments) {
            val fileReader = try {
                FileReader(fileName)
            } catch (e: FileNotFoundException) {
                code = 1
                complain(err, fileName, "No such file or directory")
                continue
            }
            fileReader.use {
                val text = it.buffered().readText().replace(System.lineSeparator(), "\n")
                val counts = wc(text)
                total = Counts(
                    total.lines + counts.lines,
                    total.words + counts.words,
                    total.chars + counts.chars
                )
                output.printCounts(counts, fileName)
            }
        }
        if (arguments.size > 1) {
            output.printCounts(total, "total")
        }
        return code
    }

    private fun wc(text: String): Counts {
        return Counts(
            text.lines().size - 1,
            WORD_REGEX.findAll(text).count(),
            text.length
        )
    }

    private fun Writer.printCounts(counts: Counts, fileName: String?) {
        appendLine(
            " %7d %7d %7d %s".format(counts.lines, counts.words, counts.chars, fileName ?: "")
        )
        flush()
    }

    private data class Counts(val lines: Int, val words: Int, val chars: Int)
}
