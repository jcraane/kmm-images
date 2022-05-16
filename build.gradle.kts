buildscript {
    repositories {
        google()
//        jcenter()
        mavenCentral()
        gradlePluginPortal()
//        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
//        maven { setUrl("http://kotlin.bintray.com/kotlin-eap") }
//        maven { setUrl("http://kotlin.bintray.com/kotlin-dev") }
//        maven { setUrl("https://kotlin.bintray.com/kotlinx") }
//        maven { setUrl("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
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
        jcenter()
        mavenCentral()
    }
}
