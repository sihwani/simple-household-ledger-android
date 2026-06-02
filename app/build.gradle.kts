import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseSigningFile = rootProject.file("key.properties")
val releaseSigningProperties = Properties()
val releaseSigningKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")

if (releaseSigningFile.isFile) {
    releaseSigningFile.inputStream().use { input ->
        releaseSigningProperties.load(input)
    }
}

val missingReleaseSigningKeys = releaseSigningKeys.filter { key ->
    releaseSigningProperties.getProperty(key).isNullOrBlank()
}
val hasReleaseSigningConfig = releaseSigningFile.isFile && missingReleaseSigningKeys.isEmpty()

android {
    namespace = "com.sihwani.simpleledger"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sihwani.simpleledger"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigningConfig) {
            create("release") {
                storeFile = file(releaseSigningProperties.getProperty("storeFile"))
                storePassword = releaseSigningProperties.getProperty("storePassword")
                keyAlias = releaseSigningProperties.getProperty("keyAlias")
                keyPassword = releaseSigningProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xskip-metadata-version-check"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

gradle.taskGraph.whenReady {
    val isReleaseOutputRequested = allTasks.any { task ->
        task.path == ":app:assembleRelease" || task.path == ":app:bundleRelease"
    }

    if (isReleaseOutputRequested && !hasReleaseSigningConfig) {
        val reason = if (!releaseSigningFile.isFile) {
            "key.properties file was not found."
        } else {
            "key.properties is missing: ${missingReleaseSigningKeys.joinToString()}"
        }

        throw GradleException(
            """
            Release signing is not configured. $reason
            Create android-app/key.properties from key.properties.example and keep it out of Git.
            Example keystore path: C:\Users\sihwa\keystores\hannun-ledger-upload.jks
            """.trimIndent()
        )
    }
}

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("com.google.android.gms:play-services-ads:25.3.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")

    implementation("io.coil-kt:coil-compose:2.7.0")
}
