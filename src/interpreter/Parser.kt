package com.boris.bash.interpreter

import com.boris.bash.executable.Command
import com.boris.bash.executable.Executable
import com.boris.bash.executable.Pipeline
import com.boris.bash.executable.ProgramCall
import com.boris.bash.executable.VariableAssignment
import com.boris.bash.executable.builtins.Builtins
import com.boris.bash.interpreter.ListUtils.indexOfFirst

internal class Parser(private val builtins: Builtins) {
    fun parse(tokens: List<Token>): Executable {
        val trimmed = tokens.dropWhile { it.type == TokenType.Whitespace }
            .takeIf { it.isNotEmpty() }
            ?: throw SyntaxException("Can't parse empty input")

        val executables = parseExecutables(trimmed)

        assert(executables.isNotEmpty())
        if (executables.size == 1) {
            return executables[0]
        }
        return Pipeline(executables)
    }

    private fun parseExecutables(tokens: List<Token>): List<Executable> {
        var i = 0
        val executables = mutableListOf<Executable>()
        var wasExecutable = false

        while (i < tokens.size) {
            when (tokens[i].type) {
                TokenType.Whitespace -> {
                    i++
                }
                TokenType.Assignment -> {
                    val (assignment, endI) = parseVariableAssignment(tokens, i)
                    executables += assignment
                    i = endI
                    wasExecutable = true
                }
                TokenType.String, TokenType.IntactString -> {
                    val (command, endI) = parseCommand(tokens, i)
                    executables += command
                    i = endI
                    wasExecutable = true
                }
                TokenType.Pipe /* else */ -> {
                    if (!wasExecutable) {
                        throw SyntaxException("Unexpected '|' symbol as position ${tokens[i].position}")
                    }
                    i++
                    wasExecutable = false
                }
            }
        }
        if (!wasExecutable) {
            throw SyntaxException("Expected a command after the '|' symbol")
        }

        return executables
    }

    private fun parseVariableAssignment(tokens: List<Token>, startIndex: Int): Pair<VariableAssignment, Int> {
        var i = startIndex
        val assignments = mutableMapOf<String, String>()

        while (i < tokens.size) {
            when (tokens[i].type) {
                TokenType.Assignment -> {
                    val endI = tokens
                        .indexOfFirst(startIndex = i + 1) {
                            it.type in listOf(TokenType.Pipe, TokenType.Whitespace)
                        }.takeIf { it != -1 }
                        ?: tokens.size

                    val varValue = tokens.subList(i + 1, endI).joinToString(separator = "") { it.text }
                    assignments[tokens[i].text] = varValue
                    i = endI
                }
                TokenType.Whitespace -> {
                    i++
                }
                TokenType.Pipe -> {
                    break
                }
                else -> throw SyntaxException("Invalid variable assignment: unexpected token at position ${tokens[i].position}")
            }
        }

        return VariableAssignment(assignments) to i
    }

    private fun parseCommand(tokens: List<Token>, startIndex: Int): Pair<Command, Int> {
        val strings = mutableListOf<String>()
        val currentString = mutableListOf<String>()

        var i = startIndex
        while (i <= tokens.lastIndex) {
            when (tokens[i].type) {
                TokenType.Pipe -> break
                TokenType.Whitespace -> {
                    if (currentString.isNotEmpty()) {
                        strings += currentString.joinToString(separator = "") { it }
                        currentString.clear()
                    }
                }
                else -> currentString += tokens[i].text
            }
            i++
        }

        if (currentString.isNotEmpty()) {
            strings += currentString.joinToString(separator = "") { it }
        }
        assert(strings.isNotEmpty())

        val commandName = strings.first()
        val commandArgs = strings.drop(1)

        builtins.findBuiltin(commandName)?.let {
            return it.getCommand(commandArgs) to i
        }

        return ProgramCall(commandName, commandArgs) to i
    }
}
