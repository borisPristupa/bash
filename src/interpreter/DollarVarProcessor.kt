package com.boris.bash.interpreter

import com.boris.bash.environment.Environment

/**
 * Substitutes all occurrences of environment variables in the [TokenType.String] tokens by their values
 */
internal class DollarVarProcessor(private val environment: Environment) : TokenProcessor {
    private val dollarVarRegex = Regex("\\\$(\\?|[a-zA-Z_][0-9a-zA-Z_]*)")

    override fun process(token: Token): Token {
        if (token.type != TokenType.String) {
            return token
        }

        val builder = StringBuilder(token.text)
        dollarVarRegex.findAll(token.text).toList().asReversed().forEach { matchResult ->
            val value = environment[matchResult.value.drop(1)] ?: ""
            builder.replace(matchResult.range.first, matchResult.range.last + 1, value)
        }
        return Token(builder.toString(), token.type, token.position)
    }
}
