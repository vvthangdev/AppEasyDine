plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.googleGmsGoogleServices)
}

android {
    namespace = "com.hust.vvthang.easydine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hust.vvthang.easydine"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "env"

    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        create("staging") {
            dimension = "env"
            applicationIdSuffix = ".stg"
            versionNameSuffix = "-stg"
        }

        create("product") {
            dimension = "env"
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:language"))
    implementation(project(":core:navigation"))

    implementation(project(":features:login"))
    implementation(project(":features:home"))

    implementation(project(":features_admin:sale"))
    implementation(project(":features_admin:area"))
    implementation(project(":features_admin:order"))
    implementation(project(":features_admin:profile"))
//    implementation(project(":features_admin:cart"))

    implementation(libs.core.splash)

    implementation(libs.hilt.android)
    implementation(project(":domain:api"))
    implementation(project(":features:cameraqr"))
    implementation(libs.firebase.auth)
    implementation(libs.google.play.services.auth)
    implementation(libs.firebase.auth)
    implementation(libs.googleid)
    kapt(libs.hilt.complier)

    implementation(libs.timber.log)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    một số dependency của Google Play Services yêu cầu bật Core Library Desugaring để chạy được trên Android cũ hơn
    coreLibraryDesugaring(libs.desugar.jdk.libs)

}

kapt {
    correctErrorTypes = true
}