package com.capoax.kmmimages.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KmmImagesPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(KmmImagesExtension.NAME, KmmImagesExtension::class.java, project)

        project.tasks.register(GenerateImagesTask.NAME, GenerateImagesTask::class.java) {
            it.imageFolder.set(extension.imageFolder.get())
            it.packageName.set(extension.packageName.get())
            it.imageInterface.set(extension.imageInterface.get())
            it.androidResFolder.set(extension.androidResFolder.get())
            it.sharedModuleFolder.set(extension.sharedModuleFolder.get())
            it.usePdf2SvgTool.set(extension.usePdf2SvgTool.get())
            it.defaultLanguage.set(extension.defaultLanguage.get())
            it.kotlinMainSourceFolder.set(extension.kotlinMainSourceFolder.get())
        }
    }
}
