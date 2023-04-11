plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rainc.viewbindingcore"
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

    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")

    api ("com.mikepenz:fastadapter:5.7.0")
    api ("com.mikepenz:fastadapter-extensions-binding:5.7.0")
    api ("com.mikepenz:fastadapter-extensions-swipe:5.7.0")
    api ("com.mikepenz:fastadapter-extensions-diff:5.7.0")

    api("com.android.databinding:viewbinding:7.4.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(project(":FaceRecognition:core:coroutines"))
}