package com.boris.bash.executable

import com.boris.bash.environment.Environment
import java.io.Reader
import java.io.Writer

internal class VariableAssignment(private val assignments: Map<String, String>) : Executable {
    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        for ((variable, value) in assignments) {
            environment[variable] = value
        }
        return 0
    }
}
