plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("com.google.firebase.crashlytics")
}

android {
    namespace = "com.rainc.firebaseglobal"
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

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    api(platform("com.google.firebase:firebase-bom:31.2.3"))
    api("com.google.firebase:firebase-analytics-ktx")
    api("com.google.firebase:firebase-firestore-ktx")
    api("com.google.firebase:firebase-crashlytics-ktx")
    api("com.google.firebase:firebase-messaging")
    api("com.google.firebase:firebase-config-ktx")
    api("com.google.firebase:firebase-auth-ktx")
    api("com.google.firebase:firebase-storage-ktx")
    api("com.google.firebase:firebase-dynamic-links-ktx")
}