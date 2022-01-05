import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("dev.jamiecraane.plugins.kmmimages")
}

kmmImagesConfig {
    imageFolder.set(project.projectDir.resolve("../images"))
    sharedModuleFolder.set(project.projectDir)
    androidSourceFolder.set("main")
    packageName.set("com.example.project")
    usePdf2SvgTool.set(true)
}

val generateImages = tasks["generateImages"]
tasks["preBuild"].dependsOn(generateImages)

repositories {
    google()
    jcenter()
    maven {
        setUrl("https://jitpack.io")
    }
    maven {
        setUrl("https://maven.google.com/")
    }
}

kotlin {
    android()
    /*ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }*/
    listOf(
        iosX64(),
        iosArm64(),
        //iosSimulatorArm64() sure all ios dependencies support this target
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.material:material:1.2.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
            }
        }
        /*val iosMain by getting
        val iosTest by getting*/
        val iosX64Main by getting
        val iosArm64Main by getting
        //val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            //iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        //val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            //iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {
        applicationId = "nl.jcraane.androidapp"
        minSdkVersion(22)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

/*val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
//    val targetDir = File(buildDir, "xcode-frameworks")
    val targetDir = findProperty("configuration.build.dir")
    from({ framework.outputDirectory })
    into(targetDir)

    doLast {
        copy {
            from("${project.rootDir}/android-app/src/commonMain/resources/ios")
            into("${targetDir}/shared.framework")
        }
    }
}*/

tasks {
    /**
     * This sets up dependencies between the plutil task and compileKotlinIos* tasks. This
     * way common is recompiled if something in generic.yaml changes (so new ios resources
     * are generated). If the generic.yaml file is not changed, the resources are considered
     * up to date by Gradle.
     */
/*    named("compileKotlinIos32") {
        dependsOn(plutil)
    }*/
    named("compileKotlinIosArm64") {
        dependsOn(generateImages)
    }
    named("compileKotlinIosX64") {
        dependsOn(generateImages)
    }

    named("linkDebugFrameworkIosX64") {
        doFirst {
            val configuration = System.getenv("CONFIGURATION")
            val sdkName = System.getenv("SDK_NAME")

            copy {
                from("${project.rootDir}/android-app/src/commonMain/resources/ios")
                into("${project.buildDir}/xcode-frameworks/$configuration/$sdkName/shared.framework")
            }
        }
    }
/*
    // And for release
    named("linkReleaseFrameworkIos") {
        doFirst {
            val configuration = System.getenv("CONFIGURATION")
            val sdkName = System.getenv("SDK_NAME")

            copy {
                from("${project.rootDir}/shared/src/commonMain/resources/ios")
                into("${project.buildDir}/xcode-frameworks/$configuration/$sdkName/shared.framework")
            }
        }
    }*/
}

//tasks.getByName("build").dependsOn(packForXcode)
