package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.executable.Command
import java.io.Reader
import java.io.Writer

internal class Exit : Command {
    companion object Builder : Command.Builder<Exit> {
        const val VAR_NAME = "EXIT"

        override val commandName = "exit"

        override fun getCommand(arguments: List<String>): Exit {
            return Exit()
        }
    }

    override val name = commandName

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        environment[VAR_NAME] = true.toString()
        return 0
    }
}
