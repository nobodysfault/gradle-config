package com.tmiyamon.config

import com.tmiyamon.config.task.GenerateSettingsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension

class ConfigPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def processVariant = { extension, BaseVariant variant ->
            def task = project.tasks.create(
                    name: "generate${variant.name.capitalize()}Settings",
                    type: GenerateSettingsTask) {
                packageName    variant.generateBuildConfig.buildConfigPackageName
                flavorName     variant.flavorName
                buildTypeName  variant.buildType.name
                variantDirName variant.dirName
            }

            variant.registerJavaGeneratingTask(task, task.outputDir())
            extension.sourceSets[variant.name].java.srcDirs += [task.outputDir()]
        }

        project.plugins.withId('com.android.application') {
            def app = project.extensions.findByType(AppExtension)
            if (app != null) {
                app.applicationVariants.all(processVariant.curry(app))
            }
        }
        project.plugins.withId('com.android.library') {
            def library = project.extensions.findByType(LibraryExtension)
            if (library != null) {
                library.libraryVariants.all(processVariant.curry(library))
            }
        }
    }
}

