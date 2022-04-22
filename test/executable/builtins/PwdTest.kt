package com.boris.bash.executable.builtins

import com.boris.bash.environmentOf
import com.boris.bash.environmentsEqual
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.Reader
import java.io.StringWriter

internal class PwdTest {
    @Test
    fun pwd() {
        testPwd(Pwd())
    }

    @Test
    fun ignoresArguments() {
        testPwd(Pwd.Builder.getCommand(listOf("hello")))
    }

    private fun testPwd(pwd: Pwd) {
        val env = environmentOf(Pwd.VAR_NAME to "hello/world")
        val envCopy = env.copy()
        val out = StringWriter()
        val err = StringWriter()

        val code = pwd.execute(Reader.nullReader(), out, err, env)
        assertEquals(0, code)
        assertEquals("hello/world\n", out.toString())
        assertEquals(0, err.buffer.length)

        Assertions.assertTrue(environmentsEqual(envCopy, env))
    }
}
