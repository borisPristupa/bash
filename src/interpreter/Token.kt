package com.boris.bash.interpreter

internal data class Token(val text: String, val type: TokenType, val position: Int)

/**
 * - [Pipe] -- '|' symbol
 * - [Assignment] -- environment variable in an assigning position (i.e. followed by the '=' symbol)
 * - [Whitespace] -- spaces and tabs
 * - [IntactString] -- single-quoted strings
 * - [String] -- double-quoted and unquoted strings
 */
internal enum class TokenType {
    Pipe, Assignment, Whitespace, IntactString, String
}
