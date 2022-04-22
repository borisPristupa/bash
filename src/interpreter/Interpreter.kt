package com.boris.bash.interpreter

import com.boris.bash.environment.Environment
import com.boris.bash.executable.Executable
import com.boris.bash.executable.builtins.Builtins

internal class Interpreter(builtins: Builtins, environment: Environment) {
    private val parser = Parser(builtins)
    private val processor = DollarVarProcessor(environment)

    fun interpret(input: String): Executable {
        return Lexer.tokenize(input)
            .map(processor::process)
            .let { parser.parse(it) }
    }
}

internal class SyntaxException(message: String) : RuntimeException(message)
