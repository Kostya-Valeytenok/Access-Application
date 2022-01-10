package com.psu.accessapplication.model

data class FaceModel(
    val eyesDistance: Double? = null,
    val lEyeAndMouthDistance: Double? = null,
    val rEyeAndMouthDistance: Double? = null,
    val mouthWidth: Double? = null,
    val noseAndMouthDistance: Double? = null,
    val lEyeAndNoseDistance: Double? = null,
    val rEyeAndNoseDistance: Double? = null,
    val faceHeight: Double? = null,
    val faceWidth: Double? = null
) {
    val modelData: String
        get() = """
            FACE ATTRIBUTES: 
            eyes distance: = $eyesDistance"
            distance between left eye and mouth: = $lEyeAndMouthDistance"
            distance between right eye and Mouth: = $rEyeAndMouthDistance
            distance mouth width: = $mouthWidth
            distance between nose and mouth: = $noseAndMouthDistance
            distance between r eye and nose =  $rEyeAndNoseDistance
            distance between left eye and nose = $lEyeAndNoseDistance
            face height: = $faceHeight
            face width: = $faceWidth
        """.trimIndent()
}
