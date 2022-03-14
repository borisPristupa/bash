package com.boris.bash.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LexerTest {
    @Test
    fun empty() {
        val tokens = Lexer.tokenize("")
        val expected = emptyList<Token>()
        assertEquals(expected, tokens)
    }

    @Test
    fun strings() {
        val tokens = Lexer.tokenize("asd asd \"asd asd\" 'asd asd' \t asd\"asd\"'asd'")
        val expected = listOf(
            Token("asd", TokenType.String, 0),
            Token(" ", TokenType.Whitespace, 3),
            Token("asd", TokenType.String, 4),
            Token(" ", TokenType.Whitespace, 7),
            Token("asd asd", TokenType.String, 8),
            Token(" ", TokenType.Whitespace, 17),
            Token("asd asd", TokenType.IntactString, 18),
            Token(" \t ", TokenType.Whitespace, 27),
            Token("asd", TokenType.String, 30),
            Token("asd", TokenType.String, 33),
            Token("asd", TokenType.IntactString, 38),
        )
        assertEquals(expected, tokens)
    }

    @Test
    fun unmatchedQuote() {
        assertThrows<SyntaxException> {
            Lexer.tokenize("than he said: 'to stay awake, you need")
        }
        assertThrows<SyntaxException> {
            Lexer.tokenize("i wont repeat: \"to stay awake, you need")
        }
    }

    @Test
    fun assignment() {
        val tokens = Lexer.tokenize("a=asd b=bsd c= 1=")
        val expected = listOf(
            Token("a", TokenType.Assignment, 0),
            Token("asd", TokenType.String, 2),
            Token(" ", TokenType.Whitespace, 5),
            Token("b", TokenType.Assignment, 6),
            Token("bsd", TokenType.String, 8),
            Token(" ", TokenType.Whitespace, 11),
            Token("c", TokenType.Assignment, 12),
            Token(" ", TokenType.Whitespace, 14),
            Token("1=", TokenType.String, 15),
        )
        assertEquals(expected, tokens)
    }

    @Test
    fun pipes() {
        val tokens = Lexer.tokenize("a|b || c  |")
        val expected = listOf(
            Token("a", TokenType.String, 0),
            Token("|", TokenType.Pipe, 1),
            Token("b", TokenType.String, 2),
            Token(" ", TokenType.Whitespace, 3),
            Token("|", TokenType.Pipe, 4),
            Token("|", TokenType.Pipe, 5),
            Token(" ", TokenType.Whitespace, 6),
            Token("c", TokenType.String, 7),
            Token("  ", TokenType.Whitespace, 8),
            Token("|", TokenType.Pipe, 10),
        )
        assertEquals(expected, tokens)
    }
}
