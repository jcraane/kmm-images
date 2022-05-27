package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.ImageConverter
import com.capoax.kmmimages.core.converters.convertImage
import org.gradle.api.logging.Logger
import java.io.File

class IOSImageConverter(
    private val outputFolder: File,
    private val logger: Logger) : ImageConverter {

    val pngConversions = mapOf(
        "75%" to "3x",
        "50%" to "2x",
        "25%" to ""
    )

    val assetsFolder = outputFolder.resolve("Assets.xcassets")

    init {
        assetsFolder.deleteRecursively()
        assetsFolder.mkdir()
        Contents().writeTo(assetsFolder)
    }

    override fun convertPng(sourceImage: ImageConverter.SourceImage, defaultLanguage: String) {
        logger.debug("IOSImageConverter.convertPng: $sourceImage")
        val imageName = sourceImage.name
        val imageSetFolder = assetsFolder.resolve("$imageName.imageSet").apply { mkdirs() }
        val imageList = pngConversions.flatMap { (resize, scale) ->
            val arguments = mutableListOf<String>()
            if (resize.isNotEmpty()) {
                arguments.addAll(listOf("-resize", resize))
            }

            val scaleName = if (scale.isEmpty()) "" else "@$scale"
            sourceImage.outputFiles("png", scaleName).map { output ->

                convertImage(output.input.file, imageSetFolder, output.name, arguments)

                Image(
                        filename = output.name,
                        scale = if (scale.isEmpty()) "1x" else scale,
                        locale = output.locale,
                        appearances = output.appearance?.appearances
                )
            }

        }

        Contents(imageList.toSet(), properties = Properties(localizable = sourceImage.localizable)).also {
            it.writeTo(imageSetFolder)
        }
    }

    override fun convertPdf(sourceImage: ImageConverter.SourceImage, usePdf2SvgTool: Boolean, defaultLanguage: String) {
        logger.debug("IOSImageConverter.convertPdf: $sourceImage")
        copyImage(sourceImage, Properties(preservesVectorRepresentation = true))
    }

    override fun convertJpg(sourceImage: ImageConverter.SourceImage, defaultLanguage: String) {
        logger.debug("IOSImageConverter.convertJpg: $sourceImage")
        copyImage(sourceImage)
    }

    override fun convertSvg(sourceImage: ImageConverter.SourceImage, defaultLanguage: String) {
        logger.debug("IOSImageConverter.convertSvg: $sourceImage")
        val imageName = sourceImage.name
        val pdfExtension = ".pdf"

        val convertedPdf = sourceImage.copy(files = sourceImage.outputFiles("pdf").map { output ->
            val sourceImageFolder = output.input.file.parentFile
            convertImage(
                    sourceImage = output.input.file,
                    outputFolder = sourceImageFolder,
                    outputName = output.name
            )

            output.input.copy(file = File(sourceImageFolder, output.name))
        })

        convertPdf(convertedPdf, usePdf2SvgTool = false, defaultLanguage = defaultLanguage)

        convertedPdf.files.forEach { it.file.delete() }
    }

    private fun copyImage(sourceImage: ImageConverter.SourceImage, properties: Properties? = null) {
        val imageName = sourceImage.name
        val imageSetFolder = assetsFolder.resolve("$imageName.imageSet").apply { mkdirs() }

        val imageList = sourceImage.outputFiles().map { output ->

            output.input.file.copyTo(imageSetFolder.resolve(output.name), overwrite = true)

            Image(
                    filename = output.name,
                    locale = output.locale,
                    appearances = output.appearance?.appearances
            )
        }

        Contents(images = imageList.toSet(), properties = (properties ?: Properties()).copy(localizable = sourceImage.localizable)).also {
            it.writeTo(imageSetFolder)
        }
    }
}

private data class OutputFile(val input: ImageConverter.SourceImage.ImageFile, val name: String, val locale: String? = null, val appearance: ImageConverter.SourceImage.Appearance?)

private fun ImageConverter.SourceImage.outputFiles(extension: String? = null, suffix: String = ""): List<OutputFile> {
        val hasDarkAppearance = files.any { it.appearance == ImageConverter.SourceImage.Appearance.DARK }
        return files.flatMap { file ->
            val localeName = file.locale?.let { "_$it" } ?: ""
            val appearances = when(file.appearance) {
                ImageConverter.SourceImage.Appearance.LIGHT -> {
                    if (hasDarkAppearance) listOf(ImageConverter.SourceImage.Appearance.LIGHT, null) else listOf(null)
                }
                ImageConverter.SourceImage.Appearance.DARK -> listOf(ImageConverter.SourceImage.Appearance.DARK)
            }
            appearances.map { appearance ->
                val appearanceName = appearance?.let { "_${appearance.name.toLowerCase()}" } ?: ""
                val outputName = "$name$localeName$appearanceName$suffix.${extension ?: file.file.extension}"
                OutputFile(file, outputName, file.locale, appearance)
            }
        }
    }

private val ImageConverter.SourceImage.Appearance.appearances: List<Appearance>
    get() {
        return listOf(Appearance(value = when(this) {
            ImageConverter.SourceImage.Appearance.LIGHT -> "light"
            ImageConverter.SourceImage.Appearance.DARK -> "dark"
        }))
    }

private val ImageConverter.SourceImage.localizable: Boolean?
    get() {
        return files.any { it.locale != null }.takeIf { it }
    }
