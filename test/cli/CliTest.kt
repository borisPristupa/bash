package com.boris.bash.cli

import com.boris.bash.emptyEnvironment
import com.boris.bash.environmentOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter

internal class CliTest {
    @Test
    fun emptyInput() {
        val (input, out, err) = getInputOutputErr(null)
        assertEquals(null, input)
        assertNotEquals(0, out.length)
        assertEquals(0, err.length)
    }

    @Test
    fun lognameEnv() {
        val (_, out, err) = getInputOutputErr(null, "LOGNAME" to "Boris", "USER" to "Some other guy")
        assertTrue(out.startsWith("Boris"))
        assertEquals(0, err.length)
    }

    @Test
    fun userEnv() {
        val (_, out, err) = getInputOutputErr(null, "USER" to "Some other guy")
        assertTrue(out.startsWith("Some other guy"))
        assertEquals(0, err.length)
    }

    @Test
    fun noEnv() {
        val (_, out, err) = getInputOutputErr(null)
        assertTrue(out.startsWith("e-bash"))
        assertEquals(0, err.length)
    }

    @Test
    fun inputMatches() {
        val (input, _, err) = getInputOutputErr("Hello world")
        assertEquals("Hello world", input?.trim())
        assertEquals(0, err.length)
    }

    @Test
    fun say() {
        val out = StringWriter()
        val err = StringWriter()

        GreetingCli(
            emptyEnvironment(),
            Reader.nullReader(),
            out,
            err
        ).say("Hello")

        assertEquals("Hello" + System.lineSeparator(), out.buffer.toString())
        assertEquals(0, err.buffer.length)
    }

    @Test
    fun complain() {
        val out = StringWriter()
        val err = StringWriter()

        GreetingCli(
            emptyEnvironment(),
            Reader.nullReader(),
            out,
            err
        ).complain("Hello")

        assertEquals(0, out.buffer.length)
        assertEquals("Hello" + System.lineSeparator(), err.buffer.toString())
    }

    private fun getInputOutputErr(input: String?, vararg env: Pair<String, String>): Triple<String?, String, String> {
        val out = StringWriter()
        val err = StringWriter()

        val readInput = GreetingCli(
            environmentOf(*env),
            input?.let { StringReader(it) } ?: Reader.nullReader(),
            out,
            err
        ).nextInput()

        return Triple(readInput, out.toString(), err.toString())
    }
}
