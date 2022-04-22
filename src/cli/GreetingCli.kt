package com.boris.bash.cli

import com.boris.bash.environment.Environment
import java.io.Reader
import java.io.Writer

/**
 * Prompts for input line by line, printing a greeting each time.
 * The greeting is constructed from the "LOGNAME" environment variable, or from the "USER" in case "LOGNAME" is absent,
 * or from the predefined text in case both environment variables are not set.
 */
internal class GreetingCli(
    private val environment: Environment,
    input: Reader,
    output: Writer,
    errOutput: Writer
) : Cli {
    private val input = input.buffered()
    private val output = output.buffered()
    private val errOutput = errOutput.buffered()

    override fun nextInput(): String? {
        say(greeting(), newLine = false)
        return input.readLine()
    }

    override fun say(message: String, newLine: Boolean) {
        output.append(message)
        if (newLine) {
            output.append(System.lineSeparator())
        }
        output.flush()
    }

    override fun complain(error: String, newLine: Boolean) {
        errOutput.append(error)
        if (newLine) {
            errOutput.append(System.lineSeparator())
        }
        errOutput.flush()
    }

    private fun greeting(): String {
        val name = environment["LOGNAME"]
            ?: environment["USER"]
            ?: "e-bash"

        return "$name$ "
    }
}
