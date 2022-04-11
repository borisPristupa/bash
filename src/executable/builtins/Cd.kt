package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.environment.VarName
import com.boris.bash.environment.homeDirectory
import com.boris.bash.environment.resolveWorkingDirectory
import com.boris.bash.executable.Command
import com.boris.bash.executable.complain
import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

internal class Cd(private val arguments: List<String>) : Command {
    companion object Builder : Command.Builder<Cd> {
        override val commandName: String
            get() = "cd"

        override fun getCommand(arguments: List<String>): Cd = Cd(arguments)
    }

    override val name: String
        get() = commandName

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        val targetPath = when (arguments.size) {
            0 -> environment.homeDirectory
            1 -> {
                val path = Path.of(arguments.first())
                environment.resolveWorkingDirectory(path)
            }
            else -> {
                complain(err, "Wrong number of arguments")
                return 1
            }
        }
        if (!Files.exists(targetPath)) {
            complain(err, "No such file of directory")
            return 2
        }
        if (Files.isRegularFile(targetPath)) {
            complain(err, "Target path is file")
            return 3
        }
        environment[VarName.WORKING_DIR.repr] = targetPath.toAbsolutePath().normalize().toString()
        return 0
    }
}
