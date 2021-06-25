package com.capoax.kmmimages.extensions

object StringExtensions {
    fun imageNameToConst(value: String) =
        camelCaseToSnakeCase(value)
        .toUpperCase()
        .replace("-", "_")
        .replace(Regex("_{2,}"), "_")

    private fun camelCaseToSnakeCase(value: String): String {
        val snakeCase = StringBuilder()
        for(character in value) {
            if(character.isUpperCase()) {
                snakeCase.append("_${character.toLowerCase()}")
            } else {
                snakeCase.append(character)
            }
        }
        return snakeCase.removePrefix("_").toString()
    }
}


