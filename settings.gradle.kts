pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = "kmm-images-composite-build"

include("android-app")
includeBuild("plugin-build")
