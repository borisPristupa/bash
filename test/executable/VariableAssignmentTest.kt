package com.boris.bash.executable

import com.boris.bash.environmentOf
import com.boris.bash.environmentsEqual
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringWriter

internal class VariableAssignmentTest {
    @Test
    fun test() {
        val env = environmentOf("hello" to "world", "aba" to "caba")
        val envCopy = env.copy()
        val change = mapOf("hello" to "it's me", "ch" to "ange")

        val out = StringWriter()
        val err = StringWriter()

        val code = VariableAssignment(change).execute(Reader.nullReader(), out, err, env)
        for ((k, v) in change) {
            envCopy[k] = v
        }

        assertEquals(0, code)
        assertEquals(0, out.buffer.length)
        assertEquals(0, err.buffer.length)
        assertTrue(environmentsEqual(envCopy, env))
    }
}
