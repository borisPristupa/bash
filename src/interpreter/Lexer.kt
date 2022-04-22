package com.boris.bash.interpreter

import com.boris.bash.interpreter.ListUtils.indexOfFirst

internal object Lexer {
    fun tokenize(input: String): List<Token> {
        var i = 0
        val tokens = mutableListOf<Token>()

        while (i < input.length) {
            when {
                input[i] == '|' -> {
                    tokens += Token("|", TokenType.Pipe, i)
                    i++
                }
                input[i].isWhitespace() -> {
                    val endI = input.indexOfFirst(startIndex = i) { !it.isWhitespace() }
                        .takeIf { it != -1 } ?: input.length
                    tokens += Token(input.substring(i until endI), TokenType.Whitespace, i)
                    i = endI
                }
                input[i] == '\'' -> {
                    val lastI = input.indexOf('\'', startIndex = i + 1).takeIf { it != -1 }
                        ?: throw SyntaxException("Unmatched \"'\" symbol at position $i")
                    tokens += Token(input.substring(i + 1 until lastI), TokenType.IntactString, i)
                    i = lastI + 1
                }
                input[i] == '"' -> {
                    val lastI = input.indexOf('"', startIndex = i + 1).takeIf { it != -1 }
                        ?: throw SyntaxException("Unmatched '\"' symbol at position $i")
                    tokens += Token(input.substring(i + 1 until lastI), TokenType.String, i)
                    i = lastI + 1
                }
                else -> {
                    val endI = input.indexOfFirst(startIndex = i) {
                        it.isWhitespace() || it in "=|'\""
                    }.takeIf { it != -1 } ?: input.length

                    if (endI < input.length && input[endI] == '=') {
                        if (checkVarName(input, i until endI)) {
                            tokens += Token(input.substring(i until endI), TokenType.Assignment, i)
                            i = endI + 1
                        } else {
                            tokens += Token(input.substring(i..endI), TokenType.String, i)
                            i = endI + 1
                        }
                    } else {
                        tokens += Token(input.substring(i until endI), TokenType.String, i)
                        i = endI
                    }
                }
            }
        }

        return tokens
    }

    private fun checkVarName(input: String, range: IntRange): Boolean {
        for (i in range) {
            when {
                input[i] == '_' -> {}
                input[i].isLetter() -> {}
                input[i].isDigit() && i > range.first -> {}
                else -> break
            }
            if (i == range.last) {
                return true
            }
        }
        return false
    }
}
