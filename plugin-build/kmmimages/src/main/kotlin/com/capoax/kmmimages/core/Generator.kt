package com.capoax.kmmimages.core

import com.android.ide.common.vectordrawable.Svg2Vector
import com.capoax.kmmimages.core.converters.ImageConverter
import com.capoax.kmmimages.core.resolvers.AndroidPathResolver
import com.capoax.kmmimages.extensions.FileExtensions
import com.capoax.kmmimages.extensions.ProcessBuilderExtensions
import com.capoax.kmmimages.extensions.StringExtensions
import java.io.File
import java.io.FileFilter
import org.gradle.api.logging.Logger
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

/**
 * @property imagesFolder The folder where the input images are located.
 * @property sharedModuleFolder Folder of the shared module.
 * @property packageName Packagename and location of the generated classes.
 */
class Generator(
    val imagesFolder: File,
    val sharedModuleFolder: File,
    val androidMainFolder: String,
    val packageName: String,
    val pathToVdTool: String,
    val logger: Logger,
    val usePdf2SvgTool: Boolean
) {
    fun generate() {
        val buildFolder = sharedModuleFolder.resolve("build/images")
        buildFolder.mkdirs()
        val androidResFolder = sharedModuleFolder.resolve("src/$androidMainFolder/res")
        val iosBuildFolder = FileExtensions.createFolderIfNotExists(buildFolder, "ios")
        val androidBuildFolder = FileExtensions.createFolderIfNotExists(buildFolder, "android")
        val androidPathResolver = AndroidPathResolver(androidBuildFolder)

        val androidImageConverter = AndroidImageConverter(androidResFolder, androidPathResolver, logger)
        val iosImageConverter = IOSImageConverter(iosBuildFolder, logger)
        val codeGenerator = CodeGenerator(packageName)

        imagesFolder
            .listFiles(FileFilter { supportedFormats.contains(it.extension) })
            ?.toList()
            ?.forEach { image ->
                ImageConverter.convert(androidImageConverter, image, usePdf2SvgTool)
                ImageConverter.convert(iosImageConverter, image, usePdf2SvgTool)
                codeGenerator.addImage(image.nameWithoutExtension)
            }

        convertAndroidSvgToVectorDrawableIfSvgsArePresent(androidResFolder, androidPathResolver)

        // Compile assets catalog
        val iosOutputFolder = sharedModuleFolder.resolve("src/commonMain/resources/ios")

        iosOutputFolder.mkdirs()
        val xcrun =
            "/usr/bin/xcrun actool ${iosImageConverter.assetsFolder.path} --compile ${iosOutputFolder.path} --platform iphoneos --minimum-deployment-target 10.0"
        val xcrunOutput = ProcessBuilderExtensions.runCommand(xcrun, sharedModuleFolder)
        logger.debug("Output of xcrun = $xcrunOutput")

        val kotlinSourceFolder = sharedModuleFolder.resolve("src").resolve("commonMain").resolve("kotlin")

        val packageFolder = kotlinSourceFolder.resolve(packageName.replace(".", "/"))
        packageFolder.mkdirs()
        val imagesFile = packageFolder.resolve("Images.kt")
        imagesFile.writeText(codeGenerator.result)
    }

    //    todo replace with inline code (svg2vector)
    private fun convertAndroidSvgToVectorDrawableIfSvgsArePresent(androidResFolder: File, androidPathResolver: AndroidPathResolver) {
        val androidDrawableFolder = FileExtensions.createFolderIfNotExists(androidResFolder, "drawable")
        val svgFolder = androidPathResolver.getSvgBuildFolder()
        val containsSvg = svgFolder.listFiles(FileFilter { it.extension.endsWith(SVG) })
            ?.toList()
            ?.isNotEmpty() == true

        if (containsSvg) {
            logger.debug("Convert svg's to Android vector drawables")
            svgFolder.listFiles().forEach { svg ->
                logger.debug("Convert $svg")
                val baos = ByteArrayOutputStream()
                val vectorDrawableName = "${svg.nameWithoutExtension}.xml"
                val outputFile = File(androidDrawableFolder, vectorDrawableName)
                val error = Svg2Vector.parseSvgToXml(svg, baos)
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
