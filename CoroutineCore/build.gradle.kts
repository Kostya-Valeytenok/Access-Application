plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rainc.coroutinecore"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    api("androidx.core:core-ktx:1.9.0")
    api ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    api ("androidx.fragment:fragment-ktx:1.5.5")
    api ("androidx.activity:activity-ktx:1.7.0-alpha03")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    api ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
}