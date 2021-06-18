package com.capoax.kmmimages.core.resolvers

import com.capoax.kmmimages.extensions.createFolderIfNotExists
import java.io.File

/**
 * Provides paths for Android related conversions so path related code is in one place and can be used by
 * multiple components.
 */
class AndroidPathResolver(
    private val androidBuildFolder: File
) {
    /**
     * @return The folder where the intermediate svg's for Andrdoi (after PDF -> SVG conversion) are stored.
     */
    fun getSvgBuildFolder() = androidBuildFolder.createFolderIfNotExists(SVG_FOLDER)

    companion object {
        private const val SVG_FOLDER = "svg"
    }
}
