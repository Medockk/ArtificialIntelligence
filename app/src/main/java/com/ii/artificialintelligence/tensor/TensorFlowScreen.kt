package com.ii.artificialintelligence.tensor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun TensorFlowLiteApp() {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var resultText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val imageClassifier = remember { CustomImageClassifier(context) }
    val coroutine = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val byteArray = stream.readBytes()
                    coroutine.launch(Dispatchers.IO) {
                        withContext(Dispatchers.Main) {
                            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            // Классификация изображения
                            bitmap?.let { bmp ->
                                val classifications = imageClassifier.classify(bmp)
                                resultText =
                                    classifications?.firstOrNull()?.categories?.joinToString("\n") {
                                        it.label
                                    } ?: "Не удалось классифицировать"
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                resultText = "Ошибка загрузки изображения"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TensorFlow Lite Classifier",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Выбрать изображение")
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Selected image",
                modifier = Modifier.size(300.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Изображение не выбрано")
            }
        }

        if (resultText.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = resultText,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
