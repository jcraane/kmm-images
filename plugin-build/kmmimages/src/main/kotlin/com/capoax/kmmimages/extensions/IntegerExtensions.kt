package com.capoax.kmmimages.extensions

val Int.indentation: String get() {
    var indentation = ""
    repeat(this) {
        indentation += "  "
    }
    return indentation
}
