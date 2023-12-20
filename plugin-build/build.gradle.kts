//import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "1.9.21"
    id("com.gradle.plugin-publish") version "1.2.1" apply false
}

allprojects {
    group = "dev.jamiecraane.plugins"

    repositories {
        google()
        mavenCentral()
    }
}
