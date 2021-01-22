package com.capoax.kmmimages.extensions

import java.io.ByteArrayInputStream

/**
 * Returns an InputSTream for this string.
 */
val String.inputStream get() = ByteArrayInputStream(this.toByteArray())

fun String.imageNameToConst() = this
    .camelCaseToSnakeCase()
    .toUpperCase()
    .replace("-", "_")
    .replace(Regex("_{2,}"), "_")

private fun String.camelCaseToSnakeCase(): String {
    val snakeCase = StringBuilder()
    for(character in this) {
        if(character.isUpperCase()) {
            snakeCase.append("_${character.toLowerCase()}")
        } else {
            snakeCase.append(character)
        }
    }
    return snakeCase.removePrefix("_").toString()
}
