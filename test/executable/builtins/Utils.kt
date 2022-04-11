package com.boris.bash.executable.builtins

import com.boris.bash.emptyEnvironment
import com.boris.bash.environment.Environment
import com.boris.bash.executable.Command
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.StringReader
import java.io.StringWriter

internal fun Command.test(
    input: String = "",
    output: String? = null,
    code: Int = 0,
    environment: Environment = emptyEnvironment()
) {
    val inputReader = StringReader(input)
    val outputWriter = StringWriter()
    val errorWriter = StringWriter()
    val actualCode = execute(inputReader, output = outputWriter, err = errorWriter, environment)
    assertEquals(code, actualCode)
    assertEquals(code == 0, errorWriter.buffer.isEmpty())
    if (output != null) {
        assertEquals(output, outputWriter.buffer.toString())
    }
}

internal fun Command.Builder<*>.getCommand(vararg args: String) = getCommand(args.toList())
