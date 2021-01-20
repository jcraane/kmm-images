package com.capoax.kmmimages.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KmmImagesPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(KmmImagesExtension.NAME, KmmImagesExtension::class.java, project)

        project.tasks.register(GenerateImagesTask.NAME, GenerateImagesTask::class.java) {
            it.input.set(extension.input.get())
        }
    }
}
