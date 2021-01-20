package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.ImageConverter
import java.io.File

class IOSImageConverter(private val assetsFolder: File) : ImageConverter {

    val pngConversions = mapOf(
        "75%" to "3x",
        "50%" to "2x",
        "25%" to ""
    )

    init {
        val folder = assetsFolder.resolve("Assets.xcassets")
        folder.deleteRecursively()
        folder.mkdir()
    }

    override fun convertPng(sourceImage: File) {
        TODO("Not yet implemented")
    }

    override fun convertPdf(sourceImage: File) {
        TODO("Not yet implemented")
    }

    override fun convertJpg(sourceImage: File) {
        TODO("Not yet implemented")
    }
}
