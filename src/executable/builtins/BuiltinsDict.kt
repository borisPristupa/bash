package com.boris.bash.executable.builtins

import com.boris.bash.executable.Command

internal class BuiltinsDict(private val builtinsDict: Map<String, Command.Builder<*>>) : Builtins {
    override fun findBuiltin(name: String): Command.Builder<*>? {
        return builtinsDict[name]
    }
}
