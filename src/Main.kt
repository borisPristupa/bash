package com.boris.bash

import kotlin.system.exitProcess

fun main() {
    val code = Shell(
        System.`in`.reader(),
        System.out.writer(),
        System.err.writer()
    ).loop()
    exitProcess(code)
}
