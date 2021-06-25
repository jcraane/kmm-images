package com.capoax.kmmimages.extensions

object IntegerExtensions {
    fun getIndentation(value: Int): String {
        var indentation = ""
        repeat(value) {
            indentation += "  "
        }
        return indentation
    }
}
