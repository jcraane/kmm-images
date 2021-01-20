package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.ImageConverter
import java.io.File

class AndroidImageConverter(private val outputFolder: File) : ImageConverter {

    val pngConversions = mapOf(
        "" to "xxxhdpi",
        "75%" to "xxhdpi",
        "50%" to "xhdpi",
        "37.5%" to "hdpi",
        "25%" to "mdpi"
    )

    override fun convertPng(sourceImage: File) {
        pngConversions.forEach { resize, density ->
            val outputFolder = outputFolder.resolve("drawable-$density")
            outputFolder.mkdirs()
            val arguments = if (!resize.isEmpty()) listOf("-resize", resize) else emptyList<String>()

//            todo convert image here
        }
    }

    override fun convertPdf(sourceImage: File) {
        val outputFolder = outputFolder.resolve("drawable")
        outputFolder.mkdirs()
        val svgFileName = "${sourceImage.nameWithoutExtension}.svg"
        //            todo convert image here
    }

    override fun convertJpg(sourceImage: File) {
        pngConversions.forEach { resize, density ->
            val outputFolder = outputFolder.resolve("drawable-$density")
            outputFolder.mkdirs()

            val imageOutputFile = File(outputFolder, sourceImage.name)
            sourceImage.copyTo(imageOutputFile, overwrite = true)
        }
    }
}
