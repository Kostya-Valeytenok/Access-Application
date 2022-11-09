package com.psu.accessapplication.demo

import android.content.Context
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

/**
 * This model doesn't have metadata, so no javadoc can be generated.
 */
class MobileFaceNet(context: Context, options: Model.Options = Model.Options.Builder().build()) {
    private val model: Model = Model.createModel(context, "mobile_face_net.tflite", options)

    fun process(inputFeature0: TensorBuffer): Outputs {
        val outputs = Outputs(model)
        model.run(arrayOf<Any>(inputFeature0.buffer), outputs.buffer)
        return outputs
    }

    fun close() {
        model.close()
    }

    class Outputs (model: Model) {
        val outputFeature0AsTensorBuffer: TensorBuffer =TensorBuffer
            .createFixedSize(model.getOutputTensorShape(0), DataType.FLOAT32)

        val buffer: Map<Int, Any>
            get() {
                val outputs: MutableMap<Int, Any> = HashMap()
                outputs[0] = outputFeature0AsTensorBuffer.buffer
                return outputs
            }
    }
}