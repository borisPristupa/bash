package com.boris.bash.executable.builtins

import com.boris.bash.environment.Environment
import com.boris.bash.environment.resolveWorkingDirectory
import com.boris.bash.environment.workingDirectory
import com.boris.bash.executable.Command
import com.boris.bash.executable.complain
import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

internal class Ls(private val arguments: List<String>) : Command {
    companion object Builder : Command.Builder<Ls> {
        override val commandName: String
            get() = "ls"

        override fun getCommand(arguments: List<String>): Ls = Ls(arguments)
    }

    override val name: String
        get() = commandName

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        val targetPath = when (arguments.size) {
            0 -> environment.workingDirectory
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
            complain(err, "Target path does not exist")
            return 2
        }
        val content = if (Files.isRegularFile(targetPath)) {
            targetPath.fileName.toString() + '\n'
        } else {
            val files = Files.list(targetPath)
            val names = files.map { it.fileName }.sorted().toArray()
            names.joinToString("\n", postfix = "\n")
        }
        output.write(content)
        return 0
    }
}
