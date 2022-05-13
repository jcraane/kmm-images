package com.capoax.kmmimages.core.converters

import com.capoax.kmmimages.extensions.ProcessBuilderExtensions
import java.io.File

interface ImageConverter {

    data class SourceImage(val name: String, val files: List<ImageFile>) {

        constructor(file: File): this(file, null)
        constructor(file: File, locale: String?): this(file.nameWithoutExtension, listOf(ImageFile(file, locale)))

        data class ImageFile(val file: File, val locale: String? = null)

        val extension: String = files.first().file.extension
        val absolutePath: String = files.first().file.absolutePath

        fun with(imageFile: File, locale: String) = copy(files = files.plus(ImageFile(imageFile, locale)))
    }

    fun convertPng(sourceImage: SourceImage)
    fun convertPdf(sourceImage: SourceImage, usePdf2SvgTool: Boolean)
    fun convertJpg(sourceImage: SourceImage)
    fun convertSvg(sourceImage: SourceImage)

    companion object {
        fun convert(imageConverter: ImageConverter, sourceImage: SourceImage, usePdf2SvgTool: Boolean) {
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


