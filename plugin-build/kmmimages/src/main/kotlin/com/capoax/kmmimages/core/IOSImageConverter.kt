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

    override fun convertPng(sourceImage: ImageConverter.SourceImage) {
        logger.debug("IOSImageConverter.convertPng: $sourceImage")
        val imageName = sourceImage.name
        val imageSetFolder = assetsFolder.resolve("$imageName.imageSet").apply { mkdirs() }
        val imageList = pngConversions.flatMap { (resize, scale) ->
            val arguments = mutableListOf<String>()
            if (resize.isNotEmpty()) {
                arguments.addAll(listOf("-resize", resize))
            }

            sourceImage.files.map { file ->
                val scaleName = if (scale.isEmpty()) "" else "@$scale"
                val localeName = file.locale?.let { "_$it" } ?: ""
                val fileName = "$imageName$localeName$scaleName.png"

                convertImage(file.file, imageSetFolder, fileName, arguments)

                Image(
                        filename = fileName,
                        scale = if (scale.isEmpty()) "1x" else scale,
                        locale = file.locale
                )
            }

        }

        Contents(imageList.toSet(), properties = Properties(localizable = sourceImage.localizable)).also {
            it.writeTo(imageSetFolder)
        }
    }

    override fun convertPdf(sourceImage: ImageConverter.SourceImage, usePdf2SvgTool: Boolean) {
        logger.debug("IOSImageConverter.convertPdf: $sourceImage")
        copyImage(sourceImage, Properties(preservesVectorRepresentation = true))
    }

    override fun convertJpg(sourceImage: ImageConverter.SourceImage) {
        logger.debug("IOSImageConverter.convertJpg: $sourceImage")
        copyImage(sourceImage)
    }

    override fun convertSvg(sourceImage: ImageConverter.SourceImage) {
        logger.debug("IOSImageConverter.convertSvg: $sourceImage")
        val imageName = sourceImage.name
        val pdfExtension = ".pdf"

        val convertedPdf = sourceImage.copy(files = sourceImage.files.map { file ->
            val sourceImageFolder = file.file.parentFile
            val localeName = file.locale?.let { "_$it" } ?: ""
            val outputName = "$imageName$localeName$pdfExtension"

            convertImage(
                    sourceImage = file.file,
                    outputFolder = sourceImageFolder,
                    outputName = outputName
            )

            file.copy(file = File(sourceImageFolder, outputName))
        })

        convertPdf(convertedPdf, usePdf2SvgTool = false)

        convertedPdf.files.forEach { it.file.delete() }
    }

    private fun copyImage(sourceImage: ImageConverter.SourceImage, properties: Properties? = null) {
        val imageName = sourceImage.name
        val imageSetFolder = assetsFolder.resolve("$imageName.imageSet").apply { mkdirs() }


        val imageList = sourceImage.files.map { file ->
            val localeName = file.locale?.let { "_$it" } ?: ""
            val fileName = "$imageName$localeName.${file.file.extension}"

            file.file.copyTo(imageSetFolder.resolve(fileName), overwrite = true)

            Image(
                    filename = fileName,
                    locale = file.locale
            )
        }


        Contents(images = imageList.toSet(), properties = (properties ?: Properties()).copy(localizable = sourceImage.localizable)).also {
            it.writeTo(imageSetFolder)
        }
    }
}

private val ImageConverter.SourceImage.localizable: Boolean?
    get() {
        return files.any { it.locale != null }.takeIf { it }
    }
