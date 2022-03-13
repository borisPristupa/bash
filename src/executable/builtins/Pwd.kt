package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.executable.Command
import java.io.Reader
import java.io.Writer

internal class Pwd : Command {
    companion object Builder : Command.Builder<Pwd> {
        const val VAR_NAME = "PWD"

        override val commandName = "pwd"

        override fun getCommand(arguments: List<String>): Pwd {
            return Pwd()
        }
    }

    override val name = commandName

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        val pwd = environment[VAR_NAME] ?: System.getProperty("user.dir")
        output.appendLine(pwd)
        output.flush()
        return 0
    }
}
