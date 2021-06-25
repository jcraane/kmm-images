package com.capoax.kmmimages.extensions

import java.io.File
import java.util.concurrent.TimeUnit

object ProcessBuilderExtensions {
    /**
     * Runs a shell command.
     *
     * @param command The command to run.
     * @param workingDir The working directory to run the command in, defaults the current working directory.
     * @param timeoutAmount The timeout for the command, defaults to 10.
     * @param timeoutUnit The time unit for the timeout, defaults to seconds.
     */
    fun runCommand(
        command: String,
        workingDir: File = File("."),
        timeoutAmount: Long = 10,
        timeoutUnit: TimeUnit = TimeUnit.SECONDS
    ): String? {
        return try {
            ProcessBuilder(command.split("\\s{1,}".toRegex()))
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

}
