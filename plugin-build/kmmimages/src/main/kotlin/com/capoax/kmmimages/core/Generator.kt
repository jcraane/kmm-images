package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.convert
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
    val logger: Logger
) {
    fun generate() {
        val buildFolder = sharedModuleFolder.resolve("build/images")
        buildFolder.mkdirs()
        val androidResFolder = sharedModuleFolder.resolve("src/$androidMainFolder/res")
        val iosBuildFolder = buildFolder.createFolderIfNotExists("ios")

        val androidImageConverter = AndroidImageConverter(androidResFolder, logger)
        val iosImageConverter = IOSImageConverter(iosBuildFolder, logger)
        val codeGenerator = CodeGenerator(packageName)

        imagesFolder
            .listFiles(FileFilter { supportedFormats.contains(it.extension) })
            ?.forEach { image ->
                androidImageConverter.convert(image)
                iosImageConverter.convert(image)
                codeGenerator.addImage(image.nameWithoutExtension)
            }

        // Convert svg
        val androidDrawableFolder = androidResFolder.createFolderIfNotExists("drawable")
        logger.debug("Run vd-tool")
        val vdTool = "$pathToVdTool -c -in ${androidDrawableFolder.path} -out ${androidDrawableFolder.path}"
        val vdToolOutput = vdTool.runCommand(sharedModuleFolder)
        logger.debug("Output of vd-tool = $vdToolOutput")

        androidDrawableFolder.deleteFiles { it.extension.endsWith("svg") }

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

    companion object {
        private val supportedFormats = listOf("svg", "pdf", "png", "jpg", "jpeg")
    }
}
