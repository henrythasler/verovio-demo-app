plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.henrythasler.sheetmusic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.henrythasler.sheetmusic"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20"
                cppFlags += "-llog"
            }
        }

        ndk {
            abiFilters += listOf("arm64-v8a")
//            abiFilters += listOf("x86_64")
//            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("../keystore.jks")
            val hasKeystore = keystoreFile.exists()
            val hasEnvVars = System.getenv("KEYSTORE_PASSWORD") != null &&
                    System.getenv("KEY_ALIAS") != null &&
                    System.getenv("KEY_PASSWORD") != null

            if (hasKeystore && hasEnvVars) {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                println("✓ Release signing configured with keystore")
            } else {
                println("⚠ Release signing not configured - missing keystore or environment variables")
                println("  Keystore exists: $hasKeystore")
                println("  Environment variables set: $hasEnvVars")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Only apply signing config if keystore exists and environment variables are set
            val releaseSigningConfig = signingConfigs.getByName("release")
            val keystoreFile = file("../keystore.jks")
            val hasKeystore = keystoreFile.exists()
            val hasEnvVars = System.getenv("KEYSTORE_PASSWORD") != null &&
                    System.getenv("KEY_ALIAS") != null &&
                    System.getenv("KEY_PASSWORD") != null

            if (hasKeystore && hasEnvVars) {
                signingConfig = releaseSigningConfig
            }
        }

        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
//    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidsvg)
    implementation(libs.androidx.material3.window.size.class1.android)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}