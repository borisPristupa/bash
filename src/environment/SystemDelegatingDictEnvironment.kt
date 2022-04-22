package com.boris.bash.environment

/**
 * This implementation maintains its own dictionary of user-defined variables, falling back to the System's environment
 * in the absence of the requested key.
 */
internal class SystemDelegatingDictEnvironment : Environment {
    private val dict = mutableMapOf<String, String>()

    override fun keys(): Set<String> {
        return dict.keys + System.getenv().keys
    }

    override fun get(key: String): String? {
        return dict[key] ?: System.getenv(key)
    }

    override fun set(key: String, value: String) {
        dict[key] = value
    }

    override fun copy(): Environment {
        return SystemDelegatingDictEnvironment().also {
            it.dict.putAll(dict)
        }
    }
}
