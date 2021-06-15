package com.capoax.kmmimages.core.converters

import com.capoax.kmmimages.extensions.runCommand
import java.io.File
import java.lang.RuntimeException

interface ImageConverter {
    fun convertPng(sourceImage: File)
    fun convertPdf(sourceImage: File, usePdf2SvgTool: Boolean)
    fun convertJpg(sourceImage: File)
    fun convertSvg(sourceImage: File)
}

fun ImageConverter.convert(sourceImage: File, usePdf2SvgTool: Boolean) {
    when (sourceImage.extension) {
        "png" -> {
            convertPng(sourceImage)
        }
        "pdf" -> {
            convertPdf(sourceImage, usePdf2SvgTool)
        }
        "jpg" -> {
            convertJpg(sourceImage)
        }
        "svg" -> {
            convertSvg(sourceImage)
        }
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
}

/**
 * Converts the sourceImage (pdf) to an svg using pdf2svg.
 */
fun convertImagePdfToSvg(
    sourceImage: File,
    outputFolder: File,
    outputName: String) {
    val pdf2svg = "pdf2svg ${sourceImage.path} ${outputFolder.path}/$outputName"
    val pdf2svgResult = pdf2svg.runCommand()
}


