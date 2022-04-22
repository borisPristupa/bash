package com.boris.bash.cli

/**
 * Represents a simple Command Line Interface for prompting the user for an input, printing him messages and errors
 */
internal interface Cli {
    fun nextInput(): String?
    fun say(message: String, newLine: Boolean = true)
    fun complain(error: String, newLine: Boolean = true)
}
