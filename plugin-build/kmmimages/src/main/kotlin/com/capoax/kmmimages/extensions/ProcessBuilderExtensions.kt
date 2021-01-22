package com.capoax.kmmimages.extensions

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Runs a shell command
 */
fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 10,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String? {
    println("run: $this")
    return try {
        ProcessBuilder(split("\\s{1,}".toRegex()))
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start().apply { waitFor(timeoutAmount, timeoutUnit) }
            .inputStream.bufferedReader().readText()
    } catch (e: java.io.IOException) {
        e.printStackTrace()
        null
    }
}
