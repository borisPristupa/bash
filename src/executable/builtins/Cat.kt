package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.executable.Command
import com.boris.bash.executable.complain
import org.apache.commons.io.input.CloseShieldReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader
import java.io.Writer

internal class Cat(arguments: List<String>) : Command {
    companion object Builder : Command.Builder<Cat> {
        override val commandName = "cat"

        override fun getCommand(arguments: List<String>): Cat {
            return Cat(arguments)
        }
    }

    private val arguments = arguments.takeIf { it.isNotEmpty() } ?: listOf("-")

    override val name = commandName

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        var code = 0
        for (argument in arguments) {
            if (argument == "-") {
                cat(CloseShieldReader.wrap(input), output)
            } else {
                val fileReader = try {
                    FileReader(argument)
                } catch (e: FileNotFoundException) {
                    code = 1
                    complain(err, argument, "No such file or directory")
                    continue
                }
                fileReader.use {
                    cat(it, output)
                }
            }
        }
        return code
    }

    private fun cat(actualInput: Reader, actualOutput: Writer) {
        actualInput.forEachLine {
            actualOutput.appendLine(it)
            actualOutput.flush()
        }
    }
}
