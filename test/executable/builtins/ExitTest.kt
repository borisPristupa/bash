package com.boris.bash.executable.builtins

import com.boris.bash.emptyEnvironment
import com.boris.bash.environmentsEqual
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringWriter

internal class ExitTest {
    @Test
    fun setsVar() {
        testExit(Exit())
    }

    @Test
    fun ignoresArguments() {
        testExit(Exit.Builder.getCommand(listOf("hello")))
    }

    private fun testExit(exit: Exit) {
        val env = emptyEnvironment()
        val envCopy = env.copy()
        val out = StringWriter()
        val err = StringWriter()

        val code = exit.execute(Reader.nullReader(), out, err, env)
        assertEquals(0, code)
        assertEquals(0, out.buffer.length)
        assertEquals(0, err.buffer.length)

        envCopy[Exit.VAR_NAME] = true.toString()
        assertTrue(environmentsEqual(envCopy, env))
    }
}
