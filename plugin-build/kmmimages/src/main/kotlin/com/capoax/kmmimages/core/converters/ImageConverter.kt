package com.capoax.kmmimages.core.converters

import java.io.File
import java.lang.RuntimeException

interface ImageConverter {
    fun convertPng(sourceImage: File)
    fun convertPdf(sourceImage: File)
    fun convertJpg(sourceImage: File)
}

fun ImageConverter.convert(sourceImage: File) {
    when (sourceImage.extension) {
        "png" -> convertPng(sourceImage)
        "pdf" -> convertPdf(sourceImage)
        "jpg" -> convertJpg(sourceImage)
        else -> throw ImageConverterError("${sourceImage.extension} not supported (${sourceImage.absolutePath})")
    }
}

class ImageConverterError(msg: String) : RuntimeException(msg)
