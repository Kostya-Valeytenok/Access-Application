plugins {
    id("com.android.application") version "7.3.1" apply false
    id ("com.android.library") version "7.3.1" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.20" apply false
    kotlin("plugin.serialization") version "1.8.20" apply true

}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}