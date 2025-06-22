plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.module.domain.api"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "env"

    productFlavors {
        create("dev") {
            dimension = "env"
            buildConfigField("String", "API_URL", "\"https://hust-cv-student-20215643.id.vn/\"")
//            buildConfigField("String", "API_URL", "\"https://622c-2001-ee0-1ac2-6b4e-9db0-dca1-4336-b957.ngrok-free.app/\"")
        }

        create("staging") {
            dimension = "env"
            buildConfigField("String", "API_URL", "\"https://www.google.com\"")
        }

        create("product") {
            dimension = "env"
            buildConfigField("String", "API_URL", "\"https://www.google.com\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(project(":core:network"))
    implementation(project(":core:utils"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.complier)
    implementation(libs.timber.log)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}