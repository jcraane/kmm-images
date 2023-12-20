plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    `maven-publish`
    id("com.gradle.plugin-publish")
}

val junitVersion: String by project
val description = "Gradle plugin for generating localizable resources for Android and iOS in a Kotlin Multiplatform Mobile project for use in the UI, android, iOS and shared framework code."

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.yaml:snakeyaml:1.27")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation(gradleApi())
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        create("KmmImages") {
            id = "dev.jamiecraane.plugins.kmmimages"
            displayName = "Gradle plugin for generating localizable resources for Android and iOS in a Kotlin Multiplatform Mobile project"
            description = "Gradle plugin for generating localizable resources for Android and iOS in a Kotlin Multiplatform Mobile project for use in the UI, android, iOS and shared framework code."
            implementationClass = "com.capoax.kmmimages.plugin.KmmImagesPlugin"
            description = description
            version = "1.0.0-alpha14"
            tags = listOf(
                "plugin",
                "gradle",
                "kmm",
                "multiplatform",
                "android",
                "ios"
            )
            website = "https://github.com/jcraane/kmm-images"
            vcsUrl = "https://github.com/jcraane/kmm-images.git"
        }
    }
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}
