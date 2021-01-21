package com.capoax.kmmimages.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class GenerateImagesTask : DefaultTask() {
    @get:Input
    @get:Option(option = "input", description = "The folder where the commons images are located.")
    abstract val imageFolder: Property<File>

    @get:OutputDirectory
    @get:Option(option = "sharedModuleFOlder", description = "The folder where the images are written")
    abstract val sharedModuleFolder: Property<File>

    @get:Input
    @get:Option(option = "packageName", description = "The package where the generated source files are placed.")
    abstract val packageName: Property<File>

    @TaskAction
    fun generate() {
//        todo implement here
    }

    companion object {
        const val NAME = "generateImages"
    }
}
