package com.boris.bash.executable.builtins

import com.boris.bash.Resources
import com.boris.bash.emptyEnvironment
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter

internal class GrepTest {
    @Test
    fun noArg() {
        val input = "Hello\nworld, hehe"
        val (code, lines, err) = getCodeLinesErr(input, "he", emptyList())

        Assertions.assertEquals(0, code)
        Assertions.assertEquals(1, lines.size)
        Assertions.assertEquals(input.nonBlankLines()[1], lines.first())
        Assertions.assertEquals(0, err.length)
    }

    @Test
    fun singleFile() {
        val (code, lines, err) = getCodeLinesErr(
            null, "lorem", listOf("-A", "1", "-i"),
            Resources.File.File2.path
        )

        Assertions.assertEquals(0, code)
        Assertions.assertEquals(Resources.File.File2.text.nonBlankLines(), lines)
        Assertions.assertEquals(0, err.length)
    }

    @Test
    fun multipleFiles() {
        val (code, lines, err) = getCodeLinesErr(
            null, "lorem", listOf("-A", "2", "-i"),
            Resources.File.File1.path, Resources.File.File2.path
        )

        fun Resources.File.greplines() =
            text.nonBlankLines().map { "$path:$it" }

        val expectedLines = Resources.File.File1.greplines() + Resources.File.File2.greplines()

        Assertions.assertEquals(0, code)
        Assertions.assertEquals(expectedLines, lines)
        Assertions.assertEquals(0, err.length)
    }

    @Test
    fun fileNotExists() {
        val (code, lines, err) = getCodeLinesErr(
            null, "lorem", listOf("-A", "1"),
            Resources.nonExistingFilePath
        )

        Assertions.assertEquals(1, code)
        Assertions.assertEquals(0, lines.size)
        Assertions.assertEquals("grep: ${Resources.nonExistingFilePath}: No such file or directory\n", err)
    }

    private fun getCodeLinesErr(
        input: String?,
        pattern: String,
        options: List<String>,
        vararg paths: String
    ): Triple<Int, List<String>, String> {
        val out = StringWriter()
        val err = StringWriter()

        val code = Grep.getCommand(listOf(pattern) + options + paths.asList()).execute(
            input?.let { StringReader(it) } ?: Reader.nullReader(),
            out,
            err,
            emptyEnvironment()
        )

        return Triple(code, out.toString().nonBlankLines(), err.toString())
    }

    private fun String.nonBlankLines(): List<String> = lines().filter(String::isNotBlank)
}
