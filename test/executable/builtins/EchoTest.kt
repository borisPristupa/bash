package com.boris.bash.executable.builtins

import com.boris.bash.emptyEnvironment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringWriter

internal class EchoTest {
    @Test
    fun noArg() {
        val out = StringWriter()
        val err = StringWriter()
        val code = Echo(emptyList()).execute(
            Reader.nullReader(),
            out,
            err,
            emptyEnvironment()
        )

        assertEquals(0, code)
        assertEquals("\n", out.toString())
        assertEquals(0, err.buffer.length)
    }

    @Test
    fun echo() {
        val out = StringWriter()
        val err = StringWriter()
        val code = Echo(listOf("hello", "world")).execute(
            Reader.nullReader(),
            out,
            err,
            emptyEnvironment()
        )

        assertEquals(0, code)
        assertEquals("hello world\n", out.toString())
        assertEquals(0, err.buffer.length)
    }

    @Test
    fun alternativeConstruction() {
        val out = StringWriter()
        val err = StringWriter()
        val code = Echo.Builder.getCommand(listOf("hello", "world")).execute(
            Reader.nullReader(),
            out,
            err,
            emptyEnvironment()
        )

        assertEquals(0, code)
        assertEquals("hello world\n", out.toString())
        assertEquals(0, err.buffer.length)
    }
}
