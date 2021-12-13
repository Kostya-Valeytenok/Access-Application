package com.psu.accessapplication.model

data class FaceModel(
    val eyesDistance: Double? = null,
    val lEyeAndMouseDistance: Double? = null,
    val rEyeAndMouseDistance: Double? = null,
    val mouthWidth: Double? = null,
    val noseAndMouseDistance: Double? = null,
    val lEyeAndNoseDistance: Double? = null,
    val rEyeAndNoseDistance: Double? = null
) {
    val modelData: String
        get() = """
            FACE ATTRIBUTES: 
            eyes distance: = $eyesDistance"
            distance between left eye and mouth: = $lEyeAndMouseDistance"
            distance between right eye and Mouth: = $rEyeAndMouseDistance
            distance mouth width: = $mouthWidth
            distance between nose and mouth: = $noseAndMouseDistance
            distance between r eye and nose =  $rEyeAndNoseDistance
            distance between left eye and nose = $lEyeAndNoseDistance
        """.trimIndent()
}
