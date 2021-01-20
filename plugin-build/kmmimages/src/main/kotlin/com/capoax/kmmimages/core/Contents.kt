package com.capoax.kmmimages.core

data class Contents(
    val images: List<Image> = emptyList(),
    val info: Info = Info()
) {
}

data class Image(
    val idiom: String = "universal",
    val filename: String,
    val scale: String?
)

data class Info(
    val version: Int = 1,
    val author: String = "CommonImages"
)
