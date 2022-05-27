buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0")
        classpath("com.github.jengelman.gradle.plugins:shadow:2.0.2")
        classpath(kotlin("gradle-plugin", version = "1.6.0"))
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
