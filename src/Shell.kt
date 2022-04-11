package com.boris.bash

import com.boris.bash.cli.Cli
import com.boris.bash.cli.GreetingCli
import com.boris.bash.environment.SystemDelegatingDictEnvironment
import com.boris.bash.executable.builtins.BuiltinsDict
import com.boris.bash.executable.builtins.Cat
import com.boris.bash.executable.builtins.Cd
import com.boris.bash.executable.builtins.Echo
import com.boris.bash.executable.builtins.Exit
import com.boris.bash.executable.builtins.Ls
import com.boris.bash.executable.builtins.Pwd
import com.boris.bash.executable.builtins.Wc
import com.boris.bash.interpreter.Interpreter
import com.boris.bash.interpreter.SyntaxException
import java.io.Reader
import java.io.Writer

/**
 * Represents a REPL, that ends if the input is closed or when the [Exit.VAR_NAME] environment variable is set to 'true'
 */
class Shell(
    private val input: Reader,
    private val output: Writer,
    private val errOutput: Writer
) {
    private val environment = SystemDelegatingDictEnvironment()
    private val cli: Cli = GreetingCli(environment, input, output, errOutput)
    private val interpreter = Interpreter(
        BuiltinsDict(
            listOf(
                Cat.Builder,
                Echo.Builder,
                Wc.Builder,
                Pwd.Builder,
                Exit.Builder,
                Ls.Builder,
                Cd.Builder
            ).associateBy { it.commandName }
        ),
        environment
    )

    fun loop(): Int {
        while (!environment[Exit.VAR_NAME].toBoolean()) {
            val line = try {
                cli.nextInput() ?: run {
                    cli.say("exit")
                    "exit"
                }
            } catch (e: Exception) {
                cli.complain("bash: Cannot read input: ${e.message ?: "Unknown error"}")
                return 1
            }
            if (line.isBlank()) {
                continue
            }
            try {
                val code = interpreter.interpret(line)
                    .execute(input, output, errOutput, environment)
                environment["?"] = code.toString()
            } catch (e: SyntaxException) {
                environment["?"] = "258"
                cli.complain("bash: Syntax error: ${e.message}")
            } catch (e: Exception) {
                environment["?"] = "1"
                cli.complain("bash: Unexpected error: ${e.message}")
            }
        }
        return environment["?"]?.toIntOrNull() ?: 0
    }
}
