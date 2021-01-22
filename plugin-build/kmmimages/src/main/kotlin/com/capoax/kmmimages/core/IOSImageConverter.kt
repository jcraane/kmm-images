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

    override fun convertPng(sourceImage: File) {
        logger.debug("IOSImageConverter.convertPng: $sourceImage")
        val imageName = sourceImage.nameWithoutExtension
        val imageSetFolder = assetsFolder.resolve("$imageName.imageSet").apply { mkdirs() }
        val imageList = mutableSetOf<Image>()
        pngConversions.forEach {  resize, scale ->
            val arguments = mutableListOf<String>()
            if (resize.isNotEmpty()) {
                arguments.addAll(listOf("-resize", resize))
            }
            val scaleName = if (scale.isEmpty()) "" else "@$scale"
            val fileName = "$imageName$scaleName.png"

            convertImage(sourceImage, imageSetFolder, fileName, arguments)

            imageList += Image(
                filename = fileName,
                scale = if (scale.isEmpty()) "1x" else scale)
        }

        val contents = Contents(imageList)
        contents.writeTo(imageSetFolder)
    }

    override fun convertPdf(sourceImage: File) {
        logger.debug("IOSImageConverter.convertPdf: $sourceImage")
        copyImage(sourceImage)
    }

    override fun convertJpg(sourceImage: File) {
        logger.debug("IOSImageConverter.convertJpg: $sourceImage")
        copyImage(sourceImage)
    }

    override fun convertSvg(sourceImage: File) {
        logger.debug("IOSImageConverter.convertSvg: $sourceImage")
        val imageName = sourceImage.nameWithoutExtension
        val pdfExtension = ".pdf"
        val sourceImageFolder = sourceImage.parentFile
        val outputName = "$imageName$pdfExtension"
        convertImage(
            sourceImage = sourceImage,
            outputFolder = sourceImageFolder,
            outputName = outputName
        )

        val convertedPdf = File(sourceImageFolder, outputName)
        convertPdf(convertedPdf)

        convertedPdf.delete()
    }

    private fun copyImage(sourceImage: File) {
        val imageName = sourceImage.nameWithoutExtension
        val imageSetFolder = assetsFolder.resolve("$imageName.imageSet").apply { mkdirs() }
    sourceImage.copyTo(imageSetFolder.resolve(sourceImage.name), overwrite = true)
        Contents(sourceImage).also {
            it.writeTo(imageSetFolder)
        }
    }
}

