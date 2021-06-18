package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.ImageConverter
import com.capoax.kmmimages.core.converters.convertImage
import com.capoax.kmmimages.core.converters.convertImagePdfToSvg
import com.capoax.kmmimages.core.resolvers.AndroidPathResolver
import org.gradle.api.logging.Logger
import java.io.File

class AndroidImageConverter(
    private val androidResFolder: File,
    private val androidPathResolver: AndroidPathResolver,
    private val logger: Logger) : ImageConverter {
    val pngConversions = mapOf(
        "" to "xxxhdpi",
        "75%" to "xxhdpi",
        "50%" to "xhdpi",
        "37.5%" to "hdpi",
        "25%" to "mdpi"
    )

    init {
        pngConversions.forEach { resize, density ->
            val outputFolder = androidResFolder.resolve("drawable-$density")
            outputFolder.deleteRecursively()
            outputFolder.mkdirs()
        }
    }

    override fun convertPng(sourceImage: File) {
        logger.debug("AndroidImageConvert.convertPng: convert $sourceImage")
        pngConversions.forEach { resize, density ->
            val outputFolder = androidResFolder.resolve("drawable-$density")
            val arguments = if (!resize.isEmpty()) listOf("-resize", resize) else emptyList<String>()

            convertImage(sourceImage, outputFolder, sourceImage.name, arguments)
        }
    }

    override fun convertPdf(sourceImage: File, usePdf2SvgTool: Boolean) {
        logger.debug("AndroidImageConvert.convertPdf: convert $sourceImage")
        val outputFolder = androidPathResolver.getSvgBuildFolder()
        outputFolder.mkdirs()
        val svgFileName = "${sourceImage.nameWithoutExtension}.svg"
        if (usePdf2SvgTool) {
            convertImagePdfToSvg(sourceImage, outputFolder, svgFileName)
        } else {
            convertImage(sourceImage, outputFolder, svgFileName)
        }
    }

    override fun convertJpg(sourceImage: File) {
        logger.debug("AndroidImageConvert.convertJpg: convert $sourceImage")
        pngConversions.forEach { resize, density ->
            val outputFolder = androidResFolder.resolve("drawable-$density")
            outputFolder.mkdirs()

            val imageOutputFile = File(outputFolder, sourceImage.name)
            sourceImage.copyTo(imageOutputFile, overwrite = true)
        }
    }

    override fun convertSvg(sourceImage: File) {
        logger.debug("AndroidImageConvert.convertSvg: convert $sourceImage")
        val destination = androidResFolder.resolve("drawable")
        destination.mkdirs()
        sourceImage.copyTo(destination.resolve(sourceImage.name), overwrite = true)
    }
}
