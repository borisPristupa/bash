package com.boris.bash.executable.builtins

import com.boris.bash.Resources
import com.boris.bash.emptyEnvironment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter

internal class WcTest {
    @Test
    fun noArg() {
        val (code, out, err) = getCodeOutputErr("Hello\nworld, hehe")
        val parts = out.splitWcParts()

        assertEquals(0, code)
        assertEquals(3, parts.size)
        assertEquals(1, parts[0].toIntOrNull())
        assertEquals(3, parts[1].toIntOrNull())
        assertEquals(17, parts[2].toIntOrNull())
        assertEquals(0, err.length)
    }

    @Test
    fun singleFile() {
        val (code, out, err) = getCodeOutputErr(null, Resources.File.File1.path)
        val parts = out.splitWcParts()

        assertEquals(0, code)
        assertEquals(4, parts.size)
        assertEquals(0, parts[0].toIntOrNull())
        assertEquals(8, parts[1].toIntOrNull())
        assertEquals(55, parts[2].toIntOrNull())
        assertEquals(Resources.File.File1.path, parts[3])
        assertEquals(0, err.length)
    }

    @Test
    fun fileNotExists() {
        val (code, out, err) = getCodeOutputErr(null, Resources.nonExistingFilePath)
        assertEquals(1, code)
        assertEquals(0, out.length)
        assertEquals("wc: ${Resources.nonExistingFilePath}: No such file or directory\n", err)
    }

    @Test
    fun manyFiles() {
        val (code, out, err) = getCodeOutputErr(null, Resources.File.File1.path, Resources.File.File2.path)
        val partsLines = out.lines().filter { it.isNotBlank() }.map { it.splitWcParts() }
        assertEquals(0, code)
        assertEquals(3, partsLines.size)
        assertEquals(0, err.length)

        val expected = listOf(
            listOf(0, 8, 55) to Resources.File.File1.path,
            listOf(1, 19, 123) to Resources.File.File2.path,
            listOf(1, 8 + 19, 55 + 123) to "total"
        )
        for (i in 0..2) {
            assertEquals(4, partsLines[i].size)
            assertEquals(expected[i].first[0], partsLines[i][0].toIntOrNull())
            assertEquals(expected[i].first[1], partsLines[i][1].toIntOrNull())
            assertEquals(expected[i].first[2], partsLines[i][2].toIntOrNull())
            assertEquals(expected[i].second, partsLines[i][3])
        }
    }

    @Test
    fun alternativeConstruction() {
        val out = StringWriter()
        val err = StringWriter()

        val code = Wc.Builder.getCommand(listOf(Resources.File.File1.path)).execute(
            Reader.nullReader(),
            out,
            err,
            emptyEnvironment()
        )

        val parts = out.toString().splitWcParts()

        assertEquals(0, code)
        assertEquals(4, parts.size)
        assertEquals(0, parts[0].toIntOrNull())
        assertEquals(8, parts[1].toIntOrNull())
        assertEquals(55, parts[2].toIntOrNull())
        assertEquals(Resources.File.File1.path, parts[3])
        assertEquals(0, err.toString().length)
    }

    private fun String.splitWcParts(): List<String> {
        return split(" ", "\n").filter { it.isNotEmpty() }
    }

    private fun getCodeOutputErr(input: String?, vararg paths: String): Triple<Int, String, String> {
        val out = StringWriter()
        val err = StringWriter()

        val code = Wc(paths.asList()).execute(
            input?.let { StringReader(it) } ?: Reader.nullReader(),
            out,
            err,
            emptyEnvironment()
        )

        return Triple(code, out.toString(), err.toString())
    }
}
