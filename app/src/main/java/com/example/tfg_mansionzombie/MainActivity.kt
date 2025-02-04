package com.example.tfg_mansionzombie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.animation.ObjectAnimator
import androidx.activity.ComponentActivity
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)

        val imageView = findViewById<ImageView>(R.id.imageView4)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val loadingText = findViewById<TextView>(R.id.textView)

        // Ocultamos la barra de progreso y el texto al inicio
        progressBar.visibility = View.INVISIBLE
        loadingText.visibility = View.INVISIBLE

        // Animación de la imagen
        val scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 2f, 1f).apply {
            duration = 1250
        }

        val scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 2f, 1f).apply {
            duration = 1250
        }

        scaleX.start()
        scaleY.start()

        // Lanzamos la animación de la barra de progreso y texto después de que la imagen termine
        GlobalScope.launch {
            delay(1250)  // Esperamos a que termine la animación de la imagen

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
                loadingText.visibility = View.VISIBLE
            }

            // Animamos el progreso y el texto
            launch { animateProgress(progressBar) }
            launch { changeLoadingText(loadingText) }
        }
    }

    private suspend fun animateProgress(progressBar: ProgressBar) {
        val progressSteps = listOf(30, 50, 75, 100) // Niveles de progreso
        val delays = listOf(2000L, 1000L, 3000L, 2000L) // Pausas entre progresos

        for (i in progressSteps.indices) {
            withContext(Dispatchers.Main) {
                val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, progressSteps[i]).apply {
                    duration = 1500
                    start()
                }
            }
            delay(delays[i])
        }

        // Después de que la barra de progreso llegue a 100%, hacemos invisibles el texto y la barra poco a poco
        withContext(Dispatchers.Main) {
            // Animación de desvanecimiento directamente en Kotlin
            val fadeOutProgress = AlphaAnimation(1f, 0f).apply {
                duration = 1000 // Duración de la animación de desvanecimiento
            }
            val fadeOutText = AlphaAnimation(1f, 0f).apply {
                duration = 1000 // Duración de la animación de desvanecimiento
            }

            // Aplicamos las animaciones de desvanecimiento a las vistas
            progressBar.startAnimation(fadeOutProgress)
            val loadingText = findViewById<TextView>(R.id.textView)
            loadingText.startAnimation(fadeOutText)

            // Esperamos que las animaciones terminen
            delay(1000)

            // Cambiar a la actividad Principal con animación
            val intent = Intent(this@MainActivity, Principal::class.java)
            startActivity(intent)

            // Animación de transición entre actividades
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)

            finish() // Finalizamos la actividad actual
        }
    }

    private suspend fun changeLoadingText(loadingText: TextView) {
        val loadingMessages = listOf("Cargando", "Cargando.", "Cargando..", "Cargando...")
        var index = 0

        while (true) {
            withContext(Dispatchers.Main) {
                loadingText.text = loadingMessages[index]
            }
            delay(1300) // Esperamos 1.3 segundos
            index = (index + 1) % loadingMessages.size  // Ciclar a través de los mensajes
        }
    }
}
