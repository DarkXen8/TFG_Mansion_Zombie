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

        // INICIALIZAMOS LA MUSICA DE FONDO
        val musicIntent = Intent(this, InitialMusicService::class.java)
        startService(musicIntent)


        val imageView = findViewById<ImageView>(R.id.loading_background)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val loadingText = findViewById<TextView>(R.id.textView)

        // 🔹 Asegurar que la barra de progreso y el texto empiecen ocultos
        progressBar.alpha = 0f
        progressBar.visibility = View.INVISIBLE
        loadingText.alpha = 0f
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

        // Esperamos que termine la animación de la imagen
        GlobalScope.launch {
            delay(1250)

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
                loadingText.visibility = View.VISIBLE

                // 🔹 Usamos ObjectAnimator para un mejor fade-in
                progressBar.animate().alpha(1f).setDuration(1000).start()
                loadingText.animate().alpha(1f).setDuration(1000).start()
            }

            // Iniciar animaciones adicionales
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

        // Desvanecemos la barra de progreso y el texto
        withContext(Dispatchers.Main) {
            val fadeOutProgress = AlphaAnimation(1f, 0f).apply {
                duration = 1000
                fillAfter = true
            }
            val fadeOutText = AlphaAnimation(1f, 0f).apply {
                duration = 1000
                fillAfter = true
            }

            progressBar.startAnimation(fadeOutProgress)
            val loadingText = findViewById<TextView>(R.id.textView)
            loadingText.startAnimation(fadeOutText)

            delay(1000) // Esperamos que termine la animación

            val fadeOutBackground = AlphaAnimation(1f, 0f).apply {
                duration = 1000
                fillAfter = true
            }
            val background = findViewById<ImageView>(R.id.loading_background)
            background.startAnimation(fadeOutBackground)

            delay(1000) // Esperamos que termine la animación

            // Cambiar a la actividad Principal instantáneamente sin mostrar el fondo
            val intent = Intent(this@MainActivity, Principal::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0) // Eliminar animaciones de transición
            finish() // Finalizamos la actividad actual inmediatamente
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