package com.capoax.kmmimages.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject
import java.io.File

abstract class KmmImagesExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    val imageFolder: Property<File> = objects.property(File::class.java)

    val sharedModuleFolder: Property<File> = objects.property(File::class.java)

    val androidSourceFolder: Property<String> = objects.property(String::class.java).apply {
        set("main")
    }

    val packageName: Property<String> = objects.property(String::class.java)

    val pathToVdTool: Property<String> = objects.property(String::class.java)

    companion object {
        const val NAME = "kmmImagesConfig"
    }
}

