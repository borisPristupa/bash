package com.boris.bash.executable.builtins

import com.boris.bash.Resources
import com.boris.bash.emptyEnvironment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter

internal class CatTest {
    @Test
    fun noArg() {
        val (code, out, err) = getCodeOutputErr("Hello world")
        assertEquals(0, code)
        assertEquals("Hello world\n", out)
        assertEquals(0, err.length)
    }

    @Test
    fun singleStdinArg() {
        val (code, out, err) = getCodeOutputErr("Hello world", "-")
        assertEquals(0, code)
        assertEquals("Hello world\n", out)
        assertEquals(0, err.length)
    }

    @Test
    fun singleFileArg() {
        val expected = Resources.File.File1.text + "\n"
        val (code, out, err) = getCodeOutputErr(null, Resources.File.File1.path)
        assertEquals(0, code)
        assertEquals(expected, out)
        assertEquals(0, err.length)
    }

    @Test
    fun fileNotExists() {
        val (code, out, err) = getCodeOutputErr(null, Resources.nonExistingFilePath)
        assertEquals(1, code)
        assertEquals(0, out.length)
        assertEquals("cat: ${Resources.nonExistingFilePath}: No such file or directory\n", err)
    }

    @Test
    fun multipleFiles() {
        val files = listOf(Resources.File.File1, Resources.File.File2, Resources.File.File1)
        val expected = files.joinToString(separator = "\n", postfix = "\n") { it.text }
        val (code, out, err) = getCodeOutputErr(null, *files.map { it.path }.toTypedArray())

        assertEquals(0, code)
        assertEquals(expected, out)
        assertEquals(0, err.length)
    }

    @Test
    fun mixedInput() {
        val expectedOut = Resources.File.File1.text + "\n" + "Hello\nworld\n"
        val expectedErr = "cat: ${Resources.nonExistingFilePath}: No such file or directory\n"
        val (code, out, err) = getCodeOutputErr(
            "Hello\nworld",
            Resources.File.File1.path,
            "-",
            Resources.nonExistingFilePath
        )

        assertEquals(1, code)
        assertEquals(expectedOut, out)
        assertEquals(expectedErr, err)
    }

    @Test
    fun alternativeConstruction() {
        val out = StringWriter()
        val err = StringWriter()

        val expected = Resources.File.File1.text + "\n" + "Hello\nworld\n"

        val code = Cat.Builder.getCommand(listOf(Resources.File.File1.path, "-")).execute(
            StringReader("Hello\nworld"),
            out,
            err,
            emptyEnvironment()
        )

        assertEquals(0, code)
        assertEquals(expected, out.buffer.toString())
        assertEquals(0, err.buffer.length)
    }

    private fun getCodeOutputErr(input: String?, vararg paths: String): Triple<Int, String, String> {
        val out = StringWriter()
        val err = StringWriter()

        val code = Cat(paths.asList()).execute(
            input?.let { StringReader(it) } ?: Reader.nullReader(),
            out,
            err,
            emptyEnvironment()
        )

        return Triple(code, out.toString(), err.toString())
    }
}
