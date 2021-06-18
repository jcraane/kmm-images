# KMM Images

Generate images for iOS and Android from a all supported images in the shared module. This will also generate an Images.kt that contains definitions for each image. kmm-images is a Gradle plugin that is configured in the build file of the shared module.

At the moment it does depend on some external tools for image conversion. These are described below.

# Requirements

Running the CommonImages depends on a couple of command line tools which must be installed.

## Image Magick

Image Magick is used for all image conversions (except pdf to svg) and itself uses ghostscript and potrace.

<https://imagemagick.org>

Installation:

```
brew install imagemagick
```

```
brew install ghostscript
```

```
brew install potrace
```

## pdf2svg

pdf2svg is used for the image conversion from pdf to svg. [https://formulae.brew.sh/formula/pdf2svg] and [http://cityinthesky.co.uk/opensource/pdf2svg/]

```
brew install pdf2svg
```

## vd-tool

vd-tool is a tool used to convert svg to Android vector drawables. This tool is a pre-requisite if sgv files are used. See https://www.androiddesignpatterns.com/2018/11/android-studio-svg-to-vector-cli.html for more information. To setup vd-tool do the following:

- Download the vd-tool.zip file
- Extract the contents of the zip file to a folder of choice
- Restart the IDE or terminal to make sure the path settings are taken into account.
- Configure the plugin with the path to <EXTRACTED_CONTENTS>/vd-tool/bin. The recommended way is to add vd-tool/bin to your path so the only configuration needed is the following: pathToVdTool.set("vd-tool")

Please note this tool is only required if raw svg files are used.

# Configuration

### Using the plugin via Gradle Plugin Portal

The KMMResources plugin is published via the Gradle plugin portal and can be found here: https://plugins.gradle.org/plugin/dev.jamiecraane.plugins.kmmimages

The following section describes the configuration of the kmm-images plugin in the Gradle build script, starting with an example:

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("dev.jamiecraane.plugins.kmmimages") version "1.0.0-alpha04"
}

kmmImagesConfig {
    imageFolder.set(project.projectDir.resolve("../images"))
    sharedModuleFolder.set(project.projectDir)
    androidSourceFolder.set("main")
    packageName.set("com.example.project")
    pathToVdTool.set("vd-tool")
    usePdf2SvgTool.set(true) // optional parameter
}
```

The above snippet applies the kmmimages plugin and configures it.

- imageFolder. This is the folder where the images are found which must be converted to Android and iOS. Subfolders are not supported at the moment.
- sharedModuleFolder. The path to the shared module (kmm). This project.projectDir if the plugin is configured in the shared module (which is typically the case). 
- androidSourceFolder. The name of the androidSourceFolder.
- packageName. The package to use for the generated Images class.
- pathToVdTool. The path to vd-tool. If vd-tool/bin is exported than 'vd-tool' is enough. If not, the full path to vd-tool must be specified. If the full path is needed make sure it is configurable to not dependen on hard-coded paths in the build file (else the build is not portable).
- usePdf2SvgTool. When true, uses the pdf2svg tool to convert pdf's to svg. Sometimes this yield better results than imagemagick.

Next, setup the generateImages task and hook it up into the build phase:

```kotlin
val generateImages = tasks["generateImages"]
tasks["preBuild"].dependsOn(generateImages)

tasks {
    named("compileKotlinIosArm64") {
        dependsOn(generateImages)
    }
    named("compileKotlinIosX64") {
        dependsOn(generateImages)
    }
}
```

Make sure the resources are copied into the framework by adding the following to the pac    kForXCode task (see also the build.gradle.kts file in android-app module):

```kotlin
doLast {
        copy {
            from("${project.rootDir}/android-app/src/commonMain/resources/ios")
            into("${targetDir}/shared.framework")
        }
    }
```

### Deploy and include plugin locally

When developing this plugin (or when forking this plugin and adding code to it) you may want to test this plugin in a project not included in this composite build. To do this do the following:

Navigate to the plugin-build folder and execute ```./gradlew :kmmresources:publishToMavenLocal```. This task publishes to the local maven repository.The local Maven repository stores build artefacts used by Maven and Gradle and can be found at ~/.m2/repository

Navigate to the ~/.m2/repository/dev/jamiecraane/plugins/kmmimages to see the published plugins.

In the project in which the plugin must be integrated do the following:

1. In settings.gradle.kts make sure the mavenLocal() repo is present in the pluginManagement section:

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
    }
}
```

Apply the plugin:

```kotlin
plugins {
    id("dev.jamiecraane.plugins.kmmimages") version "1.0.0-alpha04"
}
```

Run the application and you are good to go!

# Supported images

Images are expected to be at `[project folder]/common/images`

## PDF Vector

- iOS: images are copied
- Android: images are converted into xml drawables with svg

## PNG

Always provide a 4x version of the image.

- iOS: converts into @1x, @2x and @3x (respectively 25%, 50% and 75% of the 4x image)
- Android: converts into mdpi, hdpi, xdpi, xxdpi and xxxdpi (respectively 25%, 37.5%, 50%, 75% and 100% of the 4x image)

## JPG

JPG files (which should be used for photos) are not converted and have a fixed dimension. It's assumed that these are resized dynamically.

- iOS: single image copied
- Android: copied into mdpi, hdpi, xdpi, xxdpi and xxxdpi all with the same dimensions

## SVG

- iOS: images are converted to PDF and copied
- Android: images are converted into xml drawables with vd-tool

# Output

- Images.kt: `[project folder]/common/src/commonMain/[path to package]/Images.kt`
- iOS Images: compiled Assets catalog in `[project folder]/common/src/commonMain/resources/ios/Assets.car`
- Android Images: `[project folder]/common/src/main/res/drawable*`

# Known issues

## Too complex PDF
Some PDF's are complex which results in a use svg file. This sometimes can lead in Android to a STRING_TOO_LARGE error on that specific vector drawable. If this is the case, try reducing the complexity of the PDF.
