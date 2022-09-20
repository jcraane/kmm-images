package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.ImageConverter
import com.capoax.kmmimages.core.converters.convertImage
import com.capoax.kmmimages.core.converters.convertImagePdfToSvg
import com.capoax.kmmimages.core.resolvers.AndroidPathResolver
import org.gradle.api.logging.Logger
import java.io.File
import kotlin.io.path.deleteIfExists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

class AndroidImageConverter(
    private val androidResFolder: File,
    private val androidPathResolver: AndroidPathResolver,
    private val logger: Logger
) : ImageConverter {
    val pngConversions = mapOf(
        "" to "xxxhdpi",
        "75%" to "xxhdpi",
        "50%" to "xhdpi",
        "37.5%" to "hdpi",
        "25%" to "mdpi"
    )

    init {
        androidResFolder.deleteRecursively()
    }

    override fun convertPng(sourceImage: ImageConverter.SourceImage, defaultLanguage: String) {
        sourceImage.files.forEach { imageFile ->
            logger.info("AndroidImageConvert.convertPng: convert ${imageFile.file}")
            pngConversions.forEach { (resize, density) ->
                val outputFolder = getOutputFolder(
                    defaultLanguage = defaultLanguage,
                    appearance = imageFile.appearance,
                    locale = imageFile.locale,
                    density = density
                )

                val arguments = if (!resize.isEmpty()) listOf("-resize", resize) else emptyList()

                convertImage(imageFile.file, outputFolder, imageFile.file.name.withoutAppearance(), arguments)
            }
        }
    }

    override fun convertPdf(sourceImage: ImageConverter.SourceImage, usePdf2SvgTool: Boolean, defaultLanguage: String) {
        sourceImage.files.forEach { imageFile ->
            logger.info("AndroidImageConvert.convertPdf: convert ${imageFile.file}")
            val outputFolder = androidPathResolver.getSvgBuildFolder()
            outputFolder.mkdirs()
            val svgFileName = "${getSvgFileName(imageFile, defaultLanguage)}.svg"
            if (usePdf2SvgTool) {
                convertImagePdfToSvg(imageFile.file, outputFolder, svgFileName)
            } else {
                convertImage(imageFile.file, outputFolder, svgFileName)
            }
        }
    }

    override fun convertJpg(sourceImage: ImageConverter.SourceImage, defaultLanguage: String) {
        sourceImage.files.forEach { imageFile ->
            logger.info("AndroidImageConvert.convertJpg: convert ${imageFile.file}")
            pngConversions.forEach { (resize, density) ->
                val outputFolder = getOutputFolder(
                    defaultLanguage = defaultLanguage,
                    appearance = imageFile.appearance,
                    locale = imageFile.locale,
                    density = density
                )

                val imageOutputFile = File(outputFolder, imageFile.file.name.withoutAppearance())
                imageFile.file.copyTo(imageOutputFile, overwrite = true)
            }
        }
    }

    override fun convertSvg(sourceImage: ImageConverter.SourceImage, defaultLanguage: String) {
        sourceImage.files.forEach { imageFile ->
            logger.info("AndroidImageConvert.convertSvg: convert ${imageFile.file}")
            val destination = androidPathResolver.getSvgBuildFolder()
            destination.mkdirs()
            imageFile.file.copyTo(
                destination.resolve(getSvgFileName(imageFile, defaultLanguage)),
                overwrite = true
            )
        }
    }

    /**
     * Constructs a folder name based on the image density, locale & appearance
     * and creates this folder if it does not exist yet
     *
     * @return output folder name for Android drawable resource
     */
    private fun getOutputFolder(
        defaultLanguage: String,
        appearance: ImageConverter.SourceImage.Appearance? = null,
        locale: String? = null,
        density: String? = null
    ): File {
        val themeRes =
            if (appearance == ImageConverter.SourceImage.Appearance.DARK) "-night" else ""
        val localeRes = if (locale != null && locale != defaultLanguage) "-$locale" else ""
        val densityRes = if (density != null) "-$density" else ""

        val folderName = "drawable$localeRes$themeRes$densityRes"

        val outputFolder = androidResFolder.resolve(folderName)

        if (!outputFolder.isDirectory) {
            outputFolder.mkdirs()
        }

        return outputFolder
    }

    /**
     * Constructs a file name based on the image locale & appearance
     * @return file name, for example: filename_androidRes_es_night
     */
    private fun getSvgFileName(
        imageFile: ImageConverter.SourceImage.ImageFile,
        defaultLanguage: String
    ): String {
        val localePostfix = if (imageFile.locale != null && imageFile.locale != defaultLanguage) "_${imageFile.locale}" else ""
        val themePostfix = if (imageFile.appearance == ImageConverter.SourceImage.Appearance.DARK) "_night" else ""
        val fileName = imageFile.file.nameWithoutExtension.withoutAppearance()

        val postfix = if (localePostfix.isNotBlank() || themePostfix.isNotBlank()) {
            "${AndroidPathResolver.ANDROID_RES_PREFIX}$localePostfix$themePostfix"
        } else ""

        return "$fileName$postfix"
    }

    private fun String.withoutAppearance() = replace("_dark", "")
        .replace("_light", "")
}
