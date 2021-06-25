package com.capoax.kmmimages.extensions

import java.io.File
import java.io.FileFilter

object FileExtensions {
    fun createFolderIfNotExists(file: File, name: String): File {
        return file.resolve(name).apply {
            mkdirs()
        }
    }
}
