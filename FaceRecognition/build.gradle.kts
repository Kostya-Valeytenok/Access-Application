import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id ("kotlin-kapt")
}

android {
    namespace = "com.rainc.facerecognitionmodule"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
        compileSdkPreview = "UpsideDownCake"

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

    buildFeatures {
        viewBinding = true
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("io.insert-koin:koin-android:3.3.0")
    implementation ("io.insert-koin:koin-android-compat:3.3.0")
    implementation("com.github.kenglxn.QRGen:android:2.6.0")

    implementation("com.google.code.gson:gson:2.10")
    val camerax_version = "1.1.0-alpha08"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-view:1.0.0-alpha28")
    implementation ("com.google.mlkit:face-detection:16.1.5")
    implementation ("org.tensorflow:tensorflow-lite-support:0.2.0")
    implementation(project(":FaceRecognition:core:initScript"))
    implementation(project(":CoroutineCore"))
    implementation(project(":FaceRecognition:core:viewBinding"))

    implementation("com.github.bumptech.glide:glide:4.14.2")
    kapt("com.github.bumptech.glide:compiler:4.14.2")
}