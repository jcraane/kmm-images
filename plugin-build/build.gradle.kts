//import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "1.3.72" apply false
    id("com.gradle.plugin-publish") version "0.12.0" apply false
}

allprojects {
    group = "dev.jamiecraane.plugins"

    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
