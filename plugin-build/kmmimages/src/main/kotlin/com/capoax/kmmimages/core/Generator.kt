package com.capoax.kmmimages.core

import com.capoax.kmmimages.core.converters.convert
import com.capoax.kmmimages.extensions.createFolderIfNotExists
import com.capoax.kmmimages.extensions.deleteFiles
import com.capoax.kmmimages.extensions.runCommand
import java.io.File
import java.io.FileFilter

/**
 * @property imagesFolder The folder where the input images are located.
 * @property sharedModuleFolder Folder of the shared module.
 * @property packageName Packagename and location of the generated classes.
 */
class Generator(
    val imagesFolder: File,
    val sharedModuleFolder: File,
    val androidMainFolder: String,
    val packageName: String
) {
    fun generate() {
        val buildFolder = sharedModuleFolder.createFolderIfNotExists("build")
        val androidResFolder = sharedModuleFolder.resolve("src/$androidMainFolder/res")
        val iosBuildFolder = buildFolder.createFolderIfNotExists("ios")

        val androidImageConverter = AndroidImageConverter(androidResFolder)
        val iosImageConverter = IOSImageConverter(iosBuildFolder)
        val codeGenerator = CodeGenerator(packageName)

        imagesFolder.listFiles()?.forEach { image ->
            println("Convert ${image.nameWithoutExtension}")
            androidImageConverter.convert(image)
            iosImageConverter.convertJpg(image)
            codeGenerator.addImage(image.nameWithoutExtension)
        }

        // Convert svg
        val androidDrawableFolder = androidResFolder.createFolderIfNotExists("drawable")
        val svg2vd = "/usr/local/bin/svg2vd -d ${androidDrawableFolder.path}"
        val output = svg2vd.runCommand(sharedModuleFolder)
        println("Output of svg2vd = $output")
        imagesFolder.deleteFiles { it.extension.endsWith("svg") }

        // Compile assets catalog
        val iosOutputFolder = sharedModuleFolder.resolve("src/commonMain/resources/ios")

        val xcrun =
            "/usr/bin/xcrun actool ${iosImageConverter.assetsFolder.name} --compile ${iosOutputFolder.path} --platform iphoneos --minimum-deployment-target 10.0"
        val xcrunOutput = xcrun.runCommand(sharedModuleFolder)
        println("Output of xcrun = $xcrunOutput")

        val kotlinSourceFolder = sharedModuleFolder.resolve("src").resolve("commonMain").resolve("kotlin")

        val packageFolder = kotlinSourceFolder.resolve(packageName.replace(".", "/"))
        packageFolder.mkdirs()
        val imagesFile = packageFolder.resolve("Images.kt")
        imagesFile.writeText(codeGenerator.result)
    }
}
