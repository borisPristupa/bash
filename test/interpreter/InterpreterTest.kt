package com.boris.bash.interpreter

import com.boris.bash.emptyEnvironment
import com.boris.bash.executable.Pipeline
import com.boris.bash.executable.builtins.BuiltinsDict
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

internal class InterpreterTest {
    @Test
    fun interpret() {
        val executable = Interpreter(BuiltinsDict(emptyMap()), emptyEnvironment()).interpret("echo \$a | cat - file.txt")
        assertInstanceOf(Pipeline::class.java, executable)
    }
}
