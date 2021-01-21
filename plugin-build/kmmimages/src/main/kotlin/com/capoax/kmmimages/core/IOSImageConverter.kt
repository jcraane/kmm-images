package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.ImageConverter
import com.capoax.kmmimages.core.converters.convertImage
import java.io.File

class IOSImageConverter(private val outputFolder: File) : ImageConverter {

    val pngConversions = mapOf(
        "75%" to "3x",
        "50%" to "2x",
        "25%" to ""
    )

    val assetsFolder = outputFolder.resolve("Assets.xcassets")

    init {
        assetsFolder.deleteRecursively()
        assetsFolder.mkdir()
    }

    override fun convertPng(sourceImage: File) {
        val imageName = sourceImage.nameWithoutExtension
        val imageSetFolder = outputFolder.resolve("$imageName.imageSet").apply { mkdirs() }
        val imageList = mutableListOf<Image>()
        pngConversions.forEach {  resize, scale ->
            val arguments = mutableListOf<String>()
            if (resize.isNotEmpty()) {
                arguments + listOf("-resize", resize)
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
        copyImage(sourceImage)
    }

    override fun convertJpg(sourceImage: File) {
        copyImage(sourceImage)
    }

    private fun copyImage(sourceImage: File) {
        val imageName = sourceImage.nameWithoutExtension
        val imageSetFolder = outputFolder.resolve("$imageName.imageSet").apply { mkdirs() }
        imageSetFolder.resolve(imageName).delete()
        sourceImage.copyTo(imageSetFolder)

    }
}

