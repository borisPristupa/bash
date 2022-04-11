package com.boris.bash

import com.boris.bash.environment.Environment

internal class DictEnvironment(private val dict: MutableMap<String, String>) : Environment {

    override fun keys(): Set<String> {
        return dict.keys
    }

    override fun get(key: String): String? {
        return dict[key]
    }

    override fun set(key: String, value: String) {
        dict[key] = value
    }

    override fun copy(): Environment {
        return DictEnvironment(dict.toMutableMap()) // make a copy
    }
}

internal fun emptyEnvironment(): Environment {
    return DictEnvironment(mutableMapOf())
}

internal fun environmentOf(vararg pairs: Pair<String, String>): Environment {
    return DictEnvironment(mutableMapOf(*pairs))
}

internal fun environmentsEqual(env1: Environment, env2: Environment): Boolean {
    return env1.keys() == env2.keys() && env1.keys().all { env1[it] == env2[it] }
}
