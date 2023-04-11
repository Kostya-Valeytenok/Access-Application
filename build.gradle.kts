plugins {
    id("com.android.application") version "7.3.1" apply false
    id ("com.android.library") version "7.3.1" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id ("com.google.firebase.crashlytics") version "2.9.4" apply false
    id ("com.google.gms.google-services") version "4.3.15" apply false
    kotlin("plugin.serialization") version "1.8.20" apply true

}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}