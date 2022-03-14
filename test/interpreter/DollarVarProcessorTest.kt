package com.boris.bash.interpreter

import com.boris.bash.emptyEnvironment
import com.boris.bash.environmentOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DollarVarProcessorTest {
    @Test
    fun absentVar() {
        val token = Token("prefix \$hello postfix", TokenType.String, 0)
        val newToken = DollarVarProcessor(emptyEnvironment()).process(token)
        assertEquals("prefix  postfix", newToken.text)
        assertEquals(TokenType.String, newToken.type)
        assertEquals(0, newToken.position)
    }

    @Test
    fun presentVars() {
        val token = Token("prefix \$hello1 middle \$hello2 postfix", TokenType.String, 0)
        val env = environmentOf("hello1" to "w", "hello2" to "orld")
        val newToken = DollarVarProcessor(env).process(token)
        assertEquals("prefix w middle orld postfix", newToken.text)
        assertEquals(TokenType.String, newToken.type)
        assertEquals(0, newToken.position)
    }

    @Test
    fun onlyRegularStrings() {
        val processor = DollarVarProcessor(environmentOf("hello" to "world"))
        TokenType.values()
            .map { Token("\$hello", it, 0) }
            .forEach { token ->
                if (token.type != TokenType.String) {
                    assertEquals(token, processor.process(token))
                }
            }
    }
}
