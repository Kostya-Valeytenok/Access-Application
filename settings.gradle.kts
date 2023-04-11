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
include(":FaceRecognition")
include(":FaceRecognition:core:initScript")
include(":FaceRecognition:core:crypto")
include(":FaceRecognition:core:base64Tools")
include(":FaceRecognition:core:keyProvider")
include(":FaceRecognition:core:cryptoKeyGenerator")
include(":FaceRecognition:core:random")
include(":FaceRecognition:core:coroutines")
include(":FaceRecognition:core:serialization")
include(":FaceRecognition:core:viewBinding")
include(":FaceRecognition:core:cryptoSerialization")
include(":FaceRecognition:core:repository")
include(":FaceRecognition:core:auth")
include(":FaceRecognition:core:firestore")
include(":FaceRecognition:core:firebaseGlobal")

include(":FaceRecognition:data:recognitionSource")
