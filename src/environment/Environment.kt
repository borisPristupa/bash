package com.boris.bash.environment

/**
 * This class represents a key-value storage for strings
 */
internal interface Environment {
    /**
     * Returns a copy of all currently available keys in the environment
     */
    fun keys(): Set<String>

    fun get(key: String): String?
    fun set(key: String, value: String)

    fun copy(): Environment
}
