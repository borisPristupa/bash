package com.boris.bash.executable

import com.boris.bash.environment.Environment
import java.io.PipedReader
import java.io.PipedWriter
import java.io.Reader
import java.io.Writer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

internal class Pipeline(private val executables: List<Executable>) : Executable {
    private val executor = Executors.newFixedThreadPool(4)

    override fun execute(input: Reader, output: Writer, err: Writer, environment: Environment): Int {
        val inputsOutputs = buildList {
            var currentInput = input
            var pipedOutput = PipedWriter()
            var nextInput = PipedReader(pipedOutput)

            for (i in executables.indices) {
                if (i == executables.lastIndex) {
                    add(currentInput to output)
                } else {
                    add(currentInput to pipedOutput)
                    currentInput = nextInput
                    pipedOutput = PipedWriter()
                    nextInput = PipedReader(pipedOutput)
                }
            }
        }

        val futures = mutableListOf<CompletableFuture<Int>>()

        for (i in executables.indices) {
            val (actualIn, actualOut) = inputsOutputs[i]
            futures += CompletableFuture.supplyAsync({
                val code = executables[i].execute(actualIn, actualOut, err, environment.copy())
                if (i < executables.lastIndex) {
                    actualOut.close()
                }
                code
            }, executor)
        }

        executor.shutdown()

        CompletableFuture.allOf(*futures.toTypedArray()).join()
        return futures.sumOf { it.get() }
    }
}
