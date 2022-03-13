package com.boris.bash.executable

import com.boris.bash.environment.Environment
import java.io.Reader
import java.io.Writer

/**
 * Anything that may operate on streams and/or environment and return an exit code
 */
internal interface Executable {
    fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int
}
