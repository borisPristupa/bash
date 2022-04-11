package com.boris.bash.environment

import java.nio.file.Path

internal val Environment.workingDirectory: Path
    get() = Path.of(get(VarName.WORKING_DIR.repr) ?: System.getProperty("user.dir"))

internal fun Environment.resolveWorkingDirectory(path: Path): Path =
    workingDirectory.resolve(path)

internal val Environment.homeDirectory: Path
    get() = Path.of(get(VarName.HOME.repr) ?: System.getProperty("user.home"))

internal enum class VarName(val repr: String) {
    HOME("HOME"), WORKING_DIR("PWD")
}
