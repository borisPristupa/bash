package com.boris.bash.executable

import java.io.Writer

/**
 * Represents an executable, that is known by its name. It may be either a builtin command or a program
 */
internal interface Command : Executable {
    val name: String

    /**
     * Provides a generic way of supplying arguments to a command
     */
    interface Builder<T : Command> {
        val commandName: String
        fun getCommand(arguments: List<String>): T
    }
}

internal fun Command.complain(err: Writer, argument: String, message: String) {
    complain(err, "$argument: $message")
}

internal fun Command.complain(err: Writer, message: String) {
    err.appendLine("$name: $message")
    err.flush()
}
