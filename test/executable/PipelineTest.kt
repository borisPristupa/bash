package com.boris.bash.executable

import com.boris.bash.emptyEnvironment
import com.boris.bash.environmentsEqual
import com.boris.bash.executable.builtins.Cat
import com.boris.bash.executable.builtins.Echo
import com.boris.bash.executable.builtins.Exit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringReader
import java.io.StringWriter

internal class PipelineTest {
    @Test
    fun testPiping() {
        val echo = Echo(listOf("hello world"))
        val cat = Cat(listOf("-"))
        val pipe = Pipeline(listOf(echo, cat))

        val out = StringWriter()
        val err = StringWriter()
        val code = pipe.execute(StringReader.nullReader(), out, err, emptyEnvironment())

        assertEquals(0, code)
        assertEquals("hello world\n", out.toString())
        assertEquals(0, err.buffer.length)
    }

    @Test
    fun testOutputBuried() {
        val echo = Echo(listOf("hello"))
        val exit = Exit()
        val pipe = Pipeline(listOf(echo, exit))

        val out = StringWriter()
        val err = StringWriter()
        val code = pipe.execute(StringReader.nullReader(), out, err, emptyEnvironment())

        assertEquals(0, code)
        assertEquals(0, out.buffer.length)
        assertEquals(0, err.buffer.length)
    }

    @Test
    fun testEnvUnchanged() {
        val exit = Exit()
        val echo = Echo(listOf("hello"))
        val pipe = Pipeline(listOf(exit, echo))

        val out = StringWriter()
        val err = StringWriter()
        val env = emptyEnvironment()
        val code = pipe.execute(StringReader.nullReader(), out, err, env)

        assertEquals(0, code)
        assertEquals("hello\n", out.toString())
        assertEquals(0, err.buffer.length)
        assertTrue(environmentsEqual(emptyEnvironment(), env))
    }
}
