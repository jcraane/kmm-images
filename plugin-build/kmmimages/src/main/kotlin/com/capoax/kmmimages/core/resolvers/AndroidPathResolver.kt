package com.capoax.kmmimages.core.resolvers

import com.capoax.kmmimages.extensions.FileExtensions
import java.io.File

/**
 * Provides paths for Android related conversions so path related code is in one place and can be used by
 * multiple components.
 */
class AndroidPathResolver(
    private val androidBuildFolder: File
) {
    /**
     * @return The folder where the intermediate svg's for Android (after PDF -> SVG conversion) are stored.
     */
    fun getSvgBuildFolder() = FileExtensions.createFolderIfNotExists(androidBuildFolder, SVG_FOLDER)

    /**
     * Creates an SvgFile for each file in the Svg build folder
     */
    fun getSvgFiles(): List<SvgFile> = getSvgBuildFolder().listFiles()?.map { file ->
        val fullName = file.nameWithoutExtension

        // Split file name from resource folder specifications
        val separatedFileName = fullName.split(ANDROID_RES_PREFIX)
        val fileName = separatedFileName.first()

        // Construct resource folder postfix, for example: -es-night
        val folderPostFix = separatedFileName.getOrNull(1)
            ?.split("_")
            ?.drop(1)
            ?.joinToString("-")
            ?.let { "-$it" }
            ?: ""

        SvgFile(
            file = file,
            name = fileName,
            resFolder = "drawable$folderPostFix"
        )
    } ?: emptyList()

    data class SvgFile(
        val file: File,
        val name: String,
        val resFolder: String
    )

    companion object {
        private const val SVG_FOLDER = "svg"
        const val ANDROID_RES_PREFIX = "_androidRes"
    }
}
