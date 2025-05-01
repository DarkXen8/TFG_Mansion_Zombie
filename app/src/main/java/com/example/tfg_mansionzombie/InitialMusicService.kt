package com.example.tfg_mansionzombie

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class InitialMusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        // Inicializamos la música
        mediaPlayer = MediaPlayer.create(this, R.raw.intro_background_music)
        mediaPlayer.isLooping = true // Música en bucle
        mediaPlayer.setVolume(0.5f, 0.5f) // Volumen (puedes ajustar)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
