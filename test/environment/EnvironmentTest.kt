package com.boris.bash.environment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class EnvironmentTest {
    @Test
    fun keys() {
        val env = SystemDelegatingDictEnvironment()
        val dict = mapOf("a" to "b", "A" to "B", "123" to "asd")
        for ((k, v) in dict) {
            env[k] = v
        }
        assertTrue(env.keys().containsAll(dict.keys))
    }

    @Test
    fun getSet() {
        val env = SystemDelegatingDictEnvironment()
        val dict = mapOf("a" to "b", "A" to "B", "123" to "asd")
        for ((k, v) in dict) {
            env[k] = v
        }
        for (key in dict.keys) {
            assertEquals(dict[key], env[key])
        }
    }

    @Test
    fun copy() {
        val env = SystemDelegatingDictEnvironment()
        env["special"] = "very special"
        assertEquals("very special", env.copy()["special"])
    }
}
