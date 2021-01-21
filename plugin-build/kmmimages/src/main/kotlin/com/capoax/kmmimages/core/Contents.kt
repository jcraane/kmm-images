package com.capoax.kmmimages.core

import com.google.gson.Gson
import java.io.File

data class Contents(
    val images: List<Image> = emptyList(),
    val info: Info = Info()
) {
    fun writeTo(outputFolder: File) {
        val json = Gson().toJson(this)
        val contentsFile = outputFolder.resolve("Contents.json")
        contentsFile.createNewFile()
        contentsFile.writeText(json)
    }

    companion object {
        operator fun invoke(imageFile: File) = Contents(
            images = listOf(Image(filename = imageFile.name))
        )
    }
}

data class Image(
    val idiom: String = "universal",
    val filename: String,
    val scale: String? = null
)

data class Info(
    val version: Int = 1,
    val author: String = "CommonImages"
)
