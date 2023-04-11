plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rainc.coroutinecore"
    compileSdk = 33
    compileSdkPreview = "UpsideDownCake"

    defaultConfig {
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {

    api("androidx.core:core-ktx:1.9.0")
    api ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")
    api ("androidx.fragment:fragment-ktx:1.5.5")
    api ("androidx.activity:activity-ktx:1.8.0-alpha02")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    api ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("junit:junit:4.13.2")
}