package com.boris.bash.interpreter

import com.boris.bash.executable.Pipeline
import com.boris.bash.executable.ProgramCall
import com.boris.bash.executable.VariableAssignment
import com.boris.bash.executable.builtins.BuiltinsDict
import com.boris.bash.executable.builtins.Cat
import com.boris.bash.executable.builtins.Echo
import com.boris.bash.executable.builtins.Exit
import com.boris.bash.executable.builtins.Pwd
import com.boris.bash.executable.builtins.Wc
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ParserTest {
    private val parser =
        listOf(
            Cat.Builder,
            Echo.Builder,
            Wc.Builder,
            Pwd.Builder,
            Exit.Builder,
        ).associateBy { it.commandName }
            .let { Parser(BuiltinsDict(it)) }

    @Test
    fun empty() {
        assertThrows<SyntaxException> {
            parser.parse(emptyList())
        }
    }

    @Test
    fun badPipes() {
        assertThrows<SyntaxException> {
            parser.parse(
                listOf(
                    Token("|", TokenType.Pipe, 0),
                    Token("hello", TokenType.String, 1)
                )
            )
        }
        assertThrows<SyntaxException> {
            parser.parse(
                listOf(
                    Token("hello", TokenType.String, 0),
                    Token("|", TokenType.Pipe, 5)
                )
            )
        }
        assertThrows<SyntaxException> {
            parser.parse(
                listOf(
                    Token("hello", TokenType.String, 0),
                    Token("|", TokenType.Pipe, 5),
                    Token(" ", TokenType.Whitespace, 6),
                    Token("|", TokenType.Pipe, 7),
                    Token("hello", TokenType.String, 8)
                )
            )
        }
    }

    @Test
    fun builtin() {
        val executable = parser.parse(
            listOf(
                Token("cat", TokenType.String, 0)
            )
        )
        assertInstanceOf(Cat::class.java, executable)
    }

    @Test
    fun nonBuiltin() {
        val executable = parser.parse(
            listOf(
                Token("hello", TokenType.String, 0),
                Token(" ", TokenType.Whitespace, 5),
                Token("world", TokenType.String, 6)
            )
        )
        assertInstanceOf(ProgramCall::class.java, executable)
    }

    @Test
    fun assignment() {
        val executable = parser.parse(
            listOf(
                Token("hello", TokenType.Assignment, 0),
                Token(" ", TokenType.Whitespace, 6),
                Token("wo", TokenType.Assignment, 7),
                Token("rld", TokenType.String, 10),
            )
        )
        assertInstanceOf(VariableAssignment::class.java, executable)
        assertThrows<SyntaxException> {
            parser.parse(
                listOf(
                    Token("hello", TokenType.Assignment, 0),
                    Token(" ", TokenType.Whitespace, 6),
                    Token("rld", TokenType.String, 7),
                )
            )
        }
    }

    @Test
    fun pipe() {
        val executable = parser.parse(
            listOf(
                Token("hello", TokenType.Assignment, 0),
                Token("|", TokenType.Pipe, 5),
                Token("echo", TokenType.String, 6)
            )
        )
        assertInstanceOf(Pipeline::class.java, executable)
    }
}
