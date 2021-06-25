package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.convert
import com.capoax.kmmimages.core.resolvers.AndroidPathResolver
import com.capoax.kmmimages.extensions.createFolderIfNotExists
import com.capoax.kmmimages.extensions.deleteFiles
import com.capoax.kmmimages.extensions.runCommand
import java.io.File
import java.io.FileFilter
import org.gradle.api.logging.Logger

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
        val iosBuildFolder = buildFolder.createFolderIfNotExists("ios")
        val androidBuildFolder = buildFolder.createFolderIfNotExists("android")
        val androidPathResolver = AndroidPathResolver(androidBuildFolder)

        val androidImageConverter = AndroidImageConverter(androidResFolder, androidPathResolver, logger)
        val iosImageConverter = IOSImageConverter(iosBuildFolder, logger)
        val codeGenerator = CodeGenerator(packageName)

        imagesFolder
            .listFiles(FileFilter { supportedFormats.contains(it.extension) })
            ?.toList()
            ?.forEach { image ->
                androidImageConverter.convert(image, usePdf2SvgTool)
                iosImageConverter.convert(image, usePdf2SvgTool)
                codeGenerator.addImage(image.nameWithoutExtension)
            }

        convertAndroidSvgToVectorDrawableIfSvgsArePresent(androidResFolder, androidPathResolver)

        // Compile assets catalog
        val iosOutputFolder = sharedModuleFolder.resolve("src/commonMain/resources/ios")

        iosOutputFolder.mkdirs()
        val xcrun =
            "/usr/bin/xcrun actool ${iosImageConverter.assetsFolder.path} --compile ${iosOutputFolder.path} --platform iphoneos --minimum-deployment-target 10.0"
        val xcrunOutput = xcrun.runCommand(sharedModuleFolder)
        logger.debug("Output of xcrun = $xcrunOutput")

        val kotlinSourceFolder = sharedModuleFolder.resolve("src").resolve("commonMain").resolve("kotlin")

        val packageFolder = kotlinSourceFolder.resolve(packageName.replace(".", "/"))
        packageFolder.mkdirs()
        val imagesFile = packageFolder.resolve("Images.kt")
        imagesFile.writeText(codeGenerator.result)
    }

    //    todo replace with inline code (svg2vector)
    private fun convertAndroidSvgToVectorDrawableIfSvgsArePresent(androidResFolder: File, androidPathResolver: AndroidPathResolver) {
        val androidDrawableFolder = androidResFolder.createFolderIfNotExists("drawable")
        val svgFolder = androidPathResolver.getSvgBuildFolder()
        val containsSvg = svgFolder.listFiles(FileFilter { it.extension.endsWith(SVG) })
            ?.toList()
            ?.isNotEmpty() == true

        println("containsSvg = $containsSvg")
        if (containsSvg) {
            logger.debug("Run vd-tool")
            val vdTool = "$pathToVdTool -c -in ${svgFolder.path} -out ${androidDrawableFolder.path}"
            val vdToolOutput = vdTool.runCommand(sharedModuleFolder)
            logger.debug("Output of vd-tool = $vdToolOutput")
        }
    }

    companion object {
        private const val SVG = "svg"
        private val supportedFormats = listOf(SVG, "pdf", "png", "jpg", "jpeg")
    }
}
