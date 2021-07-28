package com.capoax.kmmimages.core.converters

import com.capoax.kmmimages.extensions.ProcessBuilderExtensions
import java.io.File

interface ImageConverter {
    fun convertPng(sourceImage: File)
    fun convertPdf(sourceImage: File, usePdf2SvgTool: Boolean)
    fun convertJpg(sourceImage: File)
    fun convertSvg(sourceImage: File)

    companion object {
        fun convert(imageConverter: ImageConverter, sourceImage: File, usePdf2SvgTool: Boolean) {
            when (sourceImage.extension) {
                "png" -> {
                    imageConverter.convertPng(sourceImage)
                }
                "pdf" -> {
                    imageConverter.convertPdf(sourceImage, usePdf2SvgTool)
                }
                "jpg" -> {
                    imageConverter.convertJpg(sourceImage)
                }
                "svg" -> {
                    imageConverter.convertSvg(sourceImage)
                }
                else -> throw ImageConverterError("${sourceImage.extension} not supported (${sourceImage.absolutePath})")
            }
        }
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
    val command = mutableListOf("magick", "convert", sourceImage.path)
    command.addAll(arguments)
    command.add("${outputFolder.path}/$outputName")
    val magickResult = ProcessBuilderExtensions.runCommand(command)
}

/**
 * Converts the sourceImage (pdf) to an svg using pdf2svg.
 */
fun convertImagePdfToSvg(
    sourceImage: File,
    outputFolder: File,
    outputName: String) {
    val command = listOf("pdf2svg", sourceImage.path, "${outputFolder.path}/$outputName")
    val pdf2svgResult = ProcessBuilderExtensions.runCommand(command)
}


