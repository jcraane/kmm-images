package com.capoax.kmmimages.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

abstract class KmmImagesExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    val imageFolder: Property<File> = objects.property(File::class.java)

    val sharedModuleFolder: Property<File> = objects.property(File::class.java)

    val androidResFolder: Property<File> = objects.property(File::class.java)

    val packageName: Property<String> = objects.property(String::class.java)

    val usePdf2SvgTool: Property<Boolean> = objects.property(Boolean::class.java).apply {
        set(false)
    }

    val defaultLanguage: Property<String> = objects.property(String::class.java).apply {
        set("en")
    }

    val kotlinMainSourceFolder: Property<String> = objects.property(String::class.java).apply {
        set("commonMain")
    }

    companion object {
        const val NAME = "kmmImagesConfig"
    }
}

