package com.capoax.kmmimages.core.converters

import com.capoax.kmmimages.extensions.runCommand
import java.io.File
import java.lang.RuntimeException

interface ImageConverter {
    fun convertPng(sourceImage: File)
    fun convertPdf(sourceImage: File)
    fun convertJpg(sourceImage: File)
}

fun ImageConverter.convert(sourceImage: File) {
    when (sourceImage.extension) {
        "png" -> convertPng(sourceImage)
        "pdf" -> convertPdf(sourceImage)
        "jpg" -> convertJpg(sourceImage)
        else -> throw ImageConverterError("${sourceImage.extension} not supported (${sourceImage.absolutePath})")
    }
}

class ImageConverterError(msg: String) : RuntimeException(msg)

/**
 * Converts image using https://imagemagick.org/. This could be changed in the future to a Java library if needed.
 */
fun convertImage(
    sourceImage: File,
    outputFolder: File,
    outputName: String,
    arguments: List<String> = emptyList()) {
    val magick = "/usr/local/bin/magick convert ${sourceImage.path} ${arguments.joinToString(" ")} ${outputFolder.path}/$outputName"
    val magickResult = magick.runCommand()
    println("Output of magick = $magickResult")
}
