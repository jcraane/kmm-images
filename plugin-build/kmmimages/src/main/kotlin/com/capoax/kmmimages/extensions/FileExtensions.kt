package com.capoax.kmmimages.extensions

import java.io.File
import java.io.FileFilter

fun File.createFolderIfNotExists(name: String): File {
    return this.resolve(name).apply {
        mkdirs()
    }
}

fun File.deleteFiles(filter: (File) -> Boolean) {
    listFiles(FileFilter {
        filter(it)
    })?.forEach { file ->
        file.delete()
    }
}
