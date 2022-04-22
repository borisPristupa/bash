package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.executable.Command
import java.io.Reader
import java.io.Writer

internal class Echo(private val arguments: List<String>) : Command {
    companion object Builder : Command.Builder<Echo> {
        override val commandName = "echo"

        override fun getCommand(arguments: List<String>): Echo {
            return Echo(arguments)
        }
    }

    override val name = commandName

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        val line = arguments.joinToString(separator = " ") { it }
        output.appendLine(line)
        output.flush()
        return 0
    }
}
