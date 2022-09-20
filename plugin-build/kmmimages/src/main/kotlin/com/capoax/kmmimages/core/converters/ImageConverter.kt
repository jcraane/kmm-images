package com.capoax.kmmimages.core.converters

import com.capoax.kmmimages.extensions.ProcessBuilderExtensions
import java.io.File

interface ImageConverter {

    data class SourceImage(val name: String, val files: List<ImageFile>) {

        enum class Appearance {
            LIGHT,
            DARK
        }

        constructor(file: File): this(file, null)
        constructor(file: File, locale: String?): this(file.nameWithoutExtension.nameWithoutAppearanceSpecifier, listOf(ImageFile(file, locale, file.name.appearance ?: Appearance.LIGHT)))

        data class ImageFile(val file: File, val locale: String? = null, val appearance: Appearance = Appearance.LIGHT)

        val extension: String = files.first().file.extension
        val absolutePath: String = files.first().file.absolutePath

        /**
         * Check if all files within the SourceImage are of the same extension
         */
        fun isValid(): Boolean {
            val allFileExtensions = files.map { it.file.extension }.distinct()
            return allFileExtensions.size == 1
        }

        fun with(imageFile: File, locale: String) = copy(files = files.plus(ImageFile(imageFile, locale)))
    }

    fun convertPng(sourceImage: SourceImage, defaultLanguage: String)
    fun convertPdf(sourceImage: SourceImage, usePdf2SvgTool: Boolean, defaultLanguage: String)
    fun convertJpg(sourceImage: SourceImage, defaultLanguage: String)
    fun convertSvg(sourceImage: SourceImage, defaultLanguage: String)

    companion object {
        fun convert(imageConverter: ImageConverter, sourceImage: SourceImage, usePdf2SvgTool: Boolean, defaultLanguage: String) {
            if (!sourceImage.isValid()) {
                throw IllegalStateException("Multiple images with different extensions found for ${sourceImage.name}, make sure there are no duplicates")
            }

            when (sourceImage.extension) {
                "png" -> {
                    imageConverter.convertPng(sourceImage, defaultLanguage)
                }
                "pdf" -> {
                    imageConverter.convertPdf(sourceImage, usePdf2SvgTool, defaultLanguage)
                }
                "jpg" -> {
                    imageConverter.convertJpg(sourceImage, defaultLanguage)
                }
                "svg" -> {
                    imageConverter.convertSvg(sourceImage, defaultLanguage)
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

private val String.appearance: ImageConverter.SourceImage.Appearance?
    get() = ImageConverter.SourceImage.Appearance.values().firstOrNull { this.contains("_${it.name}", true) }

private val String.nameWithoutAppearanceSpecifier: String
    get() {
        var result = this
        ImageConverter.SourceImage.Appearance.values()
                .forEach {
                    result = result.replace("_${it.name}", "", true)
                }
        return result
    }
