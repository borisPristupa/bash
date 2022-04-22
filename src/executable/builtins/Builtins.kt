package com.boris.bash.executable.builtins

import com.boris.bash.executable.Command

/**
 * A utility for searching for a builtin command by some string representation, typically by its name
 */
internal interface Builtins {
    fun findBuiltin(name: String): Command.Builder<*>?
}
