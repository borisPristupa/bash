package com.boris.bash

import java.io.FileReader
import java.nio.file.Path

internal object Resources {
    enum class File(fileName: String) {
        File1("test_input_1.txt"), File2("test_input_2.txt");

        val path = Path.of("resources", fileName).toString()
        val text = FileReader(path).use { it.readText() }
            .lines().filter { it.isNotBlank() }.joinToString(separator = "\n") { it }
    }

    val nonExistingFilePath = Path.of("resources", "does not exist").toString()
}
