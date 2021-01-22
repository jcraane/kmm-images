# KMM Images

Generate images for iOS and Android from a all supported images in the shared module. This will also generate an Images.kt that contains definitions for each image. kmm-images is a Gradle plugin that is configured in the build file of the shared module.

At the moment it does depend on some external tools for image conversion. These are described below.

# Requirements

Running the CommonImages depends on a couple of command line tools which must be installed.

## Image Magick

Image Magick is used for all image conversions and itself uses ghostscript and potrace.

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

## vd-tool

vd-tool is a tool used to convert svg to Android vector drawables. This tool is a pre-requisite if sgv files are used. See https://www.androiddesignpatterns.com/2018/11/android-studio-svg-to-vector-cli.html for more information. To setup vd-tool do the following:

- Download the vd-tool.zip file
- Extract the contents of the zip file to a folder of choice
- Configure the plugin with the path to vd-tool. The recommended way is to add vd-tool/bin to your path so the only configuration needed is the following: pathToVdTool.set("vd-tool")
- Restart the IDE or terminal to make sure the path settings are taken into account.

Please note this tool is only required if raw svg files are used.

# Configuration

The following section describes the configuration of the kmm-images plugin in the Gradle build script, starting with an example:

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("dev.jamiecraane.plugins.kmmimages") version "1.0.0-alpha02"
}

kmmImagesConfig {
    imageFolder.set(project.projectDir.resolve("../images"))
    sharedModuleFolder.set(project.projectDir)
    androidSourceFolder.set("main")
    packageName.set("com.example.project")
    pathToVdTool.set("vd-tool")
}
```

The above snippet applies the kmmimages plugin and configures it.

- imageFolder. This is the folder where the images are found which must be converted to Android and iOS. Subfolders are not supported at the moment.
- sharedModuleFolder. The path to the shared module (kmm). This project.projectDir if the plugin is configured in the shared module (which is typically the case). 
- androidSourceFolder. The name of the androidSourceFolder.
- packageName. The package to use for the generated Images class.
- pathToVdTool. The path to vd-tool. If vd-tool/bin is exported than 'vd-tool' is enough. If not, the full path to vd-tool must be specified. If the full path is needed make sure it is configurable to not dependen on hard-coded paths in the build file (else the build is not portable).

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

Make sure the resources are copied into the framework by adding the following to the packForXCode task (see also the build.gradle.kts file in android-app module):

```kotlin
doLast {
        copy {
            from("${project.rootDir}/android-app/src/commonMain/resources/ios")
            into("${targetDir}/shared.framework")
        }
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
