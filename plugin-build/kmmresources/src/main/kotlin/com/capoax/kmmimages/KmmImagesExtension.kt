package com.capoax.kmmimages

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject
import java.io.File

abstract class KmmImagesExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    val input: Property<File> = objects.property(File::class.java)

    companion object {
        const val NAME = "kmmImagesConfig"
    }
}

