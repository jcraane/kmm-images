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

Make sure the magick executable is present in the PATH. For iOS, the path is determined by the
building the KMM Framework from Xcode. On modern Mac's Homebrew folder `/opt/homebrew/bin` need to
be in the PATH which is done by sourcing `~/.zprofile`. By default Xcode does not source this file.
To fix that change the Shell of the Run Script step in Xcode from `/bin/sh` to `/bin/zsh -l`.

## pdf2svg

pdf2svg is used for the image conversion from pdf to svg. [https://formulae.brew.sh/formula/pdf2svg] and [http://cityinthesky.co.uk/opensource/pdf2svg/]

```
brew install pdf2svg
```
# Configuration

### Using the plugin via Gradle Plugin Portal

The KMMImages plugin is published via the Gradle plugin portal and can be found here: https://plugins.gradle.org/plugin/dev.jamiecraane.plugins.kmmimages

The following section describes the configuration of the kmm-images plugin in the Gradle build script, starting with an example:

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("dev.jamiecraane.plugins.kmmimages") version "1.0.0-alpha08"
}

kmmImagesConfig {
    imageFolder.set(project.projectDir.resolve("../images"))
    sharedModuleFolder.set(project.projectDir)
    androidSourceFolder.set("main")
    packageName.set("com.example.project")
    usePdf2SvgTool.set(true) // optional parameter
}
```

The above snippet applies the kmmimages plugin and configures it.

- imageFolder. This is the folder where the images are found which must be converted to Android and iOS. Subfolders are not supported at the moment.
- sharedModuleFolder. The path to the shared module (kmm). This project.projectDir if the plugin is configured in the shared module (which is typically the case). 
- androidSourceFolder. The name of the androidSourceFolder.
- packageName. The package to use for the generated Images class.
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

Navigate to the plugin-build folder and execute ```./gradlew :kmmimages:publishToMavenLocal```. This task publishes to the local maven repository.The local Maven repository stores build artefacts used by Maven and Gradle and can be found at ~/.m2/repository

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
    id("dev.jamiecraane.plugins.kmmimages") version "1.0.0-alpha08"
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

# Usage in Android, iOS and shared code

## Usage in Android

This section describes how to use the images from Android source code.

### Usage in xml

Since the generated images are placed in the respective res/drawable* folders, the images can be reference in XML using the standard @drawale notation, like:

```xml
<ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_flag_nl"/>

```

### Usage in code

There are two ways of using the images from the shared module in code:

1. By referencing the image using the R resource like in the following example:

```kotlin
findViewById<ImageView>(R.id.image).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.piano))
```

2. By referencing the images directly from the Images object and using an extension function (this needs to be added to your own project) to obtain the drawable for it:

```kotlin
/**
 * Extension function to obtain a drawable from an Image.
 * 
 * @param context The Android context which is used to obtain the resources from.
 * @return Drawable?
 */
fun Image.drawable(context: Context): Drawable? {
    val id = context.resources.getIdentifier(this.name, "drawable", context.packageName)
    return if (id > 0) {
        ContextCompat.getDrawable(context, id)
    } else {
        null
    }
}

// Then use this extension function like this:
findViewById<ImageView>(R.id.image).setImageDrawable(Images.PIANO.drawable(this))

/**
 * Extension function to obtain a id from an Image.
 * 
 * @param context The Android context which is used to obtain the resources from.
 * @return Int?
 */

fun Image.drawable(context: Context): Int {
    return context.resources.getIdentifier(this.name, "drawable", context.packageName)
}


//  Then use this extension function like this in Jetpack Compose:
Image( painter = painterResource(Images.PIANO.drawable(this)),
       contentDescription = "Images"
      )


```

We suggest adding this extension function to you project since this makes it easy to obtain drawables from imaegs returned from shared code, as demonstrated in the next section.

## Usage in shared code

The real power of images in the shared module is because those images can be references within the shared module. This makes it possible for example to implement a view model in shared code, which prepares the output for the view which also contains the images the view must render.

Consider the following viewmodel which resides in the share code (commonMain):

```kotlin
/**
 * This is just an example viewmodel.
 */
class MainViewModel {
    fun createViewState() = MainViewState(
        title = "My Main Screen",
        image = Images.IC_FLAG_NL
    )
}

data class MainViewState(
    val title: String,
    val image: Image
)
```

In an Android view we can use the output of the viewmodel to render the image like this (by using the above extension function):


```kotlin
val viewModel = MainViewModel()
val viewState = viewModel.createViewState()
findViewById<ImageView>(R.id.iconFromViewModel).setImageDrawable(viewState.image.drawable(this))
```

## Usage in iOS

To use the images from the shared module in iOS, we recommend adding the following extension function to your project:


```swift
/**
 * Creates a SwiftUI Image from an image in Images. Use like this: Images().IC_FLAG_NL.swiftUIImage
 */
public extension shared.Image {

    private static var sharedBundle = Bundle(for: Images.self)

    var uiImage: UIImage? {
        let sharedImage = UIImage(named: name, in: shared.Image.sharedBundle, compatibleWith: nil)
        return sharedImage
    }

    var swiftUIImage: SwiftUI.Image {
        return SwiftUI.Image(name, bundle: shared.Image.sharedBundle)
    }
}
```

You can then use this image in SwiftUI like this:

```swift
HStack {
    Images().IC_FLAG_NL.swiftUIImage
}
```

Use the uiImage to return a UIImage which does not require SwiftUI.
# Known issues

## Too complex PDF
Some PDF's are complex which results in a use svg file. This sometimes can lead in Android to a STRING_TOO_LARGE error on that specific vector drawable. If this is the case, try reducing the complexity of the PDF.
