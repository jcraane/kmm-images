package com.capoax.kmmimages.core

import com.android.ide.common.vectordrawable.Svg2Vector
import com.capoax.kmmimages.core.converters.ImageConverter
import com.capoax.kmmimages.core.resolvers.AndroidPathResolver
import com.capoax.kmmimages.extensions.FileExtensions
import com.capoax.kmmimages.extensions.ProcessBuilderExtensions
import org.gradle.api.logging.Logger
import org.gradle.internal.impldep.org.apache.commons.io.filefilter.DirectoryFileFilter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileFilter
import java.io.PrintWriter
import java.nio.file.Files

/**
 * @property imagesFolder The folder where the input images are located.
 * @property sharedModuleFolder Folder of the shared module.
 * @property androidMainFolder Android main source code folder.
 * @property packageName Packagename and location of the generated classes.
 * @property logger The logger used for outputting log messages.
 * @property usePdf2SvgTool When true, uses pdf2svg for converting pdf's to svg's instead of imagemagick.
 */
class Generator(
    val imagesFolder: File,
    val sharedModuleFolder: File,
    val androidResFolder: File,
    val packageName: String,
    val imageInterface: String?,
    val logger: Logger,
    val usePdf2SvgTool: Boolean,
    val defaultLanguage: String,
    val kotlinMainSourceFolder: String,
) {
    fun generate() {
        val buildFolder = sharedModuleFolder.resolve("build/images")
        buildFolder.mkdirs()
        val iosBuildFolder = FileExtensions.createFolderIfNotExists(buildFolder, "ios")
        val androidBuildFolder = FileExtensions.createFolderIfNotExists(buildFolder, "android")
        val androidPathResolver = AndroidPathResolver(androidBuildFolder)

        androidResFolder.mkdirs()

        val androidImageConverter = AndroidImageConverter(androidResFolder, androidPathResolver, logger)
        val iosImageConverter = IOSImageConverter(iosBuildFolder, logger)
        val codeGenerator = CodeGenerator(packageName, imageInterface)

        val supportedFilesFilter = FileFilter { supportedFormats.contains(it.extension) }

        val sourceImages = mutableListOf<ImageConverter.SourceImage>()

        fun addSourceImage(sourceImage: ImageConverter.SourceImage) {
            val index = sourceImages.indexOfFirst { it.name == sourceImage.name }
            if (index == -1) {
                sourceImages.add(sourceImage)
            } else {
                sourceImages[index] = sourceImages[index].copy(files = sourceImages[index].files.plus(sourceImage.files))
            }
        }

        imagesFolder
                .listFiles(supportedFilesFilter)
                ?.map(ImageConverter::SourceImage)
                ?.forEach { sourceImage ->
                    addSourceImage(sourceImage)
                }

        imagesFolder
                .listFiles(FileFilter { it.isDirectory })
                ?.toList()
                ?.forEach { languageFolder ->
                    val locale = languageFolder.nameWithoutExtension
                    languageFolder
                        .listFiles(supportedFilesFilter)
                        ?.forEach { imageFile ->
                            val sourceImage = ImageConverter.SourceImage(imageFile, locale)
                            addSourceImage(sourceImage)
                        }
                }

        sourceImages
                .forEach { image ->
                    ImageConverter.convert(androidImageConverter, image, usePdf2SvgTool, defaultLanguage)
                    ImageConverter.convert(iosImageConverter, image, usePdf2SvgTool, defaultLanguage)
                    codeGenerator.addImage(image.name)
                }


        convertAndroidSvgToVectorDrawableIfSvgsArePresent(androidResFolder, androidPathResolver)

        // Compile assets catalog
        val iosOutputFolder = sharedModuleFolder.resolve("src/commonMain/resources/ios")

        iosOutputFolder.mkdirs()
        val xcrunCommand = listOf("/usr/bin/xcrun", "actool", iosImageConverter.assetsFolder.path, "--compile", iosOutputFolder.path, "--platform", "iphoneos", "--minimum-deployment-target", "10.0")
        val xcrunOutput = ProcessBuilderExtensions.runCommand(xcrunCommand, sharedModuleFolder)
        logger.info("Output of xcrun = $xcrunOutput")

        val kotlinSourceFolder = sharedModuleFolder.resolve("src").resolve(kotlinMainSourceFolder).resolve("kotlin")

        val packageFolder = kotlinSourceFolder.resolve(packageName.replace(".", "/"))
        packageFolder.mkdirs()
        val imagesFile = packageFolder.resolve("Images.kt")
        imagesFile.writeText(codeGenerator.result)
    }

    private fun convertAndroidSvgToVectorDrawableIfSvgsArePresent(androidResFolder: File, androidPathResolver: AndroidPathResolver) {
        val svgFolder = androidPathResolver.getSvgBuildFolder()
        logger.info("svgFolder = $svgFolder")
        val containsSvg = svgFolder.listFiles(FileFilter { it.extension.endsWith(SVG) })
            ?.toList()
            ?.isNotEmpty() == true

        logger.debug("containsSvg = $containsSvg")
        if (containsSvg) {
            logger.info("Convert svg's to Android vector drawables")
            androidPathResolver.getSvgFiles().forEach { svg ->
                logger.debug("Convert ${svg.file}")
                val baos = ByteArrayOutputStream()

                val vectorDrawableName = "${svg.name}.xml"

                val androidDrawableFolder = FileExtensions.createFolderIfNotExists(androidResFolder, svg.resFolder)
                val outputFile = File(androidDrawableFolder, vectorDrawableName)
                val error = Svg2Vector.parseSvgToXml(svg.file, baos)

                // An error does not mean necessarily that the image could not be parsed. Generate it anyway
                if (error.isNotEmpty()) {
                    logger.error("An error occurred processing $svg, error = [$error]")
                }

                val vectorXmlContent = baos.toString()
                PrintWriter(outputFile).also { writer ->
                    writer.write(vectorXmlContent)
                    writer.flush()
                }
            }
        }
    }

    companion object {
        private const val SVG = "svg"
        private val supportedFormats = listOf(SVG, "pdf", "png", "jpg", "jpeg")
    }
}
