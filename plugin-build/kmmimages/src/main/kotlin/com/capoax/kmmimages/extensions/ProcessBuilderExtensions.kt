package com.capoax.kmmimages.extensions

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Runs a shell command
 */
fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String? = try {
    ProcessBuilder(split("\\s".toRegex()))
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start().apply { waitFor(timeoutAmount, timeoutUnit) }
        .inputStream.bufferedReader().readText()
} catch (e: java.io.IOException) {
    e.printStackTrace()
    null
}
