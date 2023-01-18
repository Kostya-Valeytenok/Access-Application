pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://www.jitpack.io" ) }
        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://www.jitpack.io" ) }
        jcenter()
    }
}
rootProject.name = "Access Application"
include(":app")
include(":FaceRecognitionModule")
include(":InitScript")
include(":ViewBindingFeature")
include(":CoroutineCore")
