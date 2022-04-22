package com.boris.bash.interpreter

/**
 * Implementors of this interface may modify the tokens, received from the lexer.
 *
 * @see DollarVarProcessor
 */
internal interface TokenProcessor {
    fun process(token: Token): Token
}
