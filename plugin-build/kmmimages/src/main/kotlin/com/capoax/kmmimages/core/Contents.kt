package com.capoax.kmmimages.core

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.File

data class Contents(
    val images: Set<Image> = emptySet(),
    val info: Info = Info(),
    val properties: Properties? = null
) {
    fun writeTo(outputFolder: File) {
        val json = Gson().toJson(this)
        val contentsFile = outputFolder.resolve("Contents.json")
        contentsFile.createNewFile()
        contentsFile.writeText(json)
    }

    companion object {
        operator fun invoke(imageFile: File, properties: Properties? = null) = Contents(
            images = setOf(Image(filename = imageFile.name)),
            properties = properties
        )
    }
}

data class Image(
    val idiom: String = "universal",
    val filename: String,
    val scale: String? = null,
    val locale: String? = null
)

data class Info(
    val version: Int = 1,
    val author: String = "CommonImages"
)

data class Properties(
    @SerializedName("preserves-vector-representation")
    val preservesVectorRepresentation: Boolean? = null,
    val localizable: Boolean? = null
)
