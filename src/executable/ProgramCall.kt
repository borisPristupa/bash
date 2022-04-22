package com.boris.bash.executable

import com.boris.bash.environment.Environment
import org.apache.commons.io.input.ReaderInputStream
import org.apache.commons.io.output.WriterOutputStream
import java.io.IOException
import java.io.PrintStream
import java.io.Reader
import java.io.Writer

internal class ProgramCall(override val name: String, arguments: List<String>) : Command {
    private val nameAndArguments = buildList {
        add(name)
        addAll(arguments)
    }

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        val stdin = System.`in`
        val stdout = System.out
        val stderr = System.err
        try {
            System.setIn(ReaderInputStream(input, Charsets.UTF_8))
            System.setOut(PrintStream(WriterOutputStream(output, Charsets.UTF_8)))
            System.setErr(PrintStream(WriterOutputStream(err, Charsets.UTF_8)))

            val processBuilder = ProcessBuilder(nameAndArguments)
                .inheritIO()
                .also {
                    val processEnvironmentView = it.environment()
                    for (key in environment.keys()) {
                        processEnvironmentView[key] = environment[key]
                    }
                }
            return try {
                processBuilder.start().onExit().get().exitValue()
            } catch (e: IOException) {
                complain(err, e.cause?.message ?: e.message ?: "Failed to execute")
                1
            } finally {
                output.flush()
                err.flush()
            }
        } finally {
            System.setIn(stdin)
            System.setOut(stdout)
            System.setErr(stderr)
        }
    }
}
