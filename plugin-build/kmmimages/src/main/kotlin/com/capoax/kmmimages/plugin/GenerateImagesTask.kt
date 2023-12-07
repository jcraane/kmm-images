package com.capoax.kmmimages.plugin

import com.capoax.kmmimages.core.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class GenerateImagesTask : DefaultTask() {
    @get:InputFiles
    @get:Option(option = "input", description = "The folder where the commons images are located.")
    abstract val imageFolder: Property<File>

    @get:OutputDirectory
    @get:Option(option = "sharedModuleFOlder", description = "The folder where the images are written")
    abstract val sharedModuleFolder: Property<File>

    @get:Input
    @get:Option(option = "packageName", description = "The package where the generated source files are placed.")
    abstract val packageName: Property<String>

    @get:Input
    @get:Option(option = "imageInterface", description = "An optional interface to add to the generated Image class.")
    abstract val imageInterface: Property<String>

    @get:Input
    @get:Option(option = "androidResFolder", description = "The source folder to generate the localizations class for Android. Defaults to main, but some multiplatform projects use androidMain.")
    abstract val androidResFolder: Property<File>

    @get:Input
    @get:Option(option = "usePdf2SvgTool", description = "If true, uses the pdf2svg tool to convert pdf's to svg, otherwise imagemagick is uses. pdf2svg might yield better results than imagemagick in this case. This setting is here for backwards compatibility reasons, default = false.")
    abstract val usePdf2SvgTool: Property<Boolean>

    @get:Input
    @get:Option(option = "defaultLanguage", description = "The default locale. Used in Android to determine which language is in the default drawable folder.")
    abstract val defaultLanguage: Property<String>

    @get:Input
    @get:Option(option = "kotlinMainSourceFolder", description = "The name of the main Kotlin source folder for the generated Images " +
            "class. Defaults to commonMain")
    abstract val kotlinMainSourceFolder: Property<String>

    @TaskAction
    fun generate() {
        project.logger.info("About to generate images for all supported platforms.")

        Generator(
            imagesFolder = imageFolder.get(),
            sharedModuleFolder = sharedModuleFolder.get(),
            androidResFolder = androidResFolder.get(),
            packageName = packageName.get(),
            imageInterface = imageInterface.get().ifEmpty { null },
            logger = project.logger,
            usePdf2SvgTool = usePdf2SvgTool.get(),
            defaultLanguage = defaultLanguage.get(),
            kotlinMainSourceFolder = kotlinMainSourceFolder.get(),
        ).generate()
    }

    companion object {
        const val NAME = "generateImages"
    }
}
