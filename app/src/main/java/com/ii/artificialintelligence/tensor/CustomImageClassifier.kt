package com.ii.artificialintelligence.tensor

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class CustomImageClassifier(context: Context) {
    private var classifier: ImageClassifier? = null

    init {
        try {
            val options = ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(1) // Топ-1 результат
                .build()

            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "model.tflite", // имя вашего файла модели
                options
            )
        } catch (e: Exception) {
            Log.e("ImageClassifier", "Error initializing classifier", e)
        }
    }

    fun classify(bitmap: Bitmap): List<Classifications>? {
        if (classifier == null) {
            Log.e("ImageClassifier", "Classifier is not initialized")
            return null
        }

        // Преобразуем Bitmap в TensorImage
        val tensorImage = TensorImage.fromBitmap(bitmap)

        // Классификация
        return classifier?.classify(tensorImage)
    }
}
