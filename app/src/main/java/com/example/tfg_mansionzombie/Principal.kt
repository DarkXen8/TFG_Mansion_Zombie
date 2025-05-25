package com.example.tfg_mansionzombie

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView

class Principal : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)  // Este es el layout que creaste para la pantalla principal

        val logo = findViewById<ImageView>(R.id.logo_img)
        val play = findViewById<Button>(R.id.play_btn)
        val inputStream = assets.open("Backgrounds/play_btn_background.png")
        val drawable = Drawable.createFromStream(inputStream, null)
        play.background = drawable
        val background = findViewById<ImageView>(R.id.imageView4)

        val cargarBtn = findViewById<Button>(R.id.load_save)
        val loadInputStream = assets.open("Backgrounds/load_btn.png")
        val loadDrawable = Drawable.createFromStream(loadInputStream, null)
        cargarBtn.background = loadDrawable

        // Recuperamos si hay partida guardada o no
        val db = openOrCreateDatabase("MansionZombieDB", MODE_PRIVATE, null)
        val cursor = db.rawQuery("SELECT * FROM save LIMIT 1", null)
        var saved = false

        if (cursor.moveToFirst()) {
            saved = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) != 0
        }

        cursor.close()
        db.close()


        if (!saved){
            cargarBtn.alpha = 0.5f
            cargarBtn.isEnabled = false
        }

        fadeIn(logo, play)
        play.setOnClickListener{
            playBtn(play)
        }
        background.setOnClickListener{
            background(background)
        }

        // Click Listeners Dificultades
        val facilbtn = findViewById<Button>(R.id.easyButton)
        val mediobtn = findViewById<Button>(R.id.mediumButton)
        val dificilbtn = findViewById<Button>(R.id.hardButton)

        facilbtn.setOnClickListener{
            launchGame(1)
        }
        mediobtn.setOnClickListener {
            launchGame(2)
        }
        dificilbtn.setOnClickListener {
            launchGame(3)
        }
        cargarBtn.setOnClickListener {
            cargarPartida()
        }
    }

    private fun fadeIn(vararg views: View){
        val fadein = AlphaAnimation(0f,1f).apply {
            duration = 500
            fillAfter = true
        }
        for (view in views){
            view.startAnimation(fadein)
        }
    }

    private fun fadeOut(vararg views: View){
        val fadeout = AlphaAnimation(1f, 0f).apply {
            duration = 500
            fillAfter = false
        }
        for (view in views){
            view.startAnimation(fadeout)
        }
    }

    private fun playBtn(play: View){
        // DIFF CONTAINER BACKGROUND
        val diffContainer = findViewById<CardView>(R.id.difficultyContainer)
        val diffContainerStream = assets.open("Backgrounds/diff_selector_background.png")
        val diffContainerDrawable = Drawable.createFromStream(diffContainerStream, null)
        diffContainer.background = diffContainerDrawable

        // EASY BACKGROUND
        val easyBtn = findViewById<Button>(R.id.easyButton)
        val easyBtnStream = assets.open("Backgrounds/easy_diff_background_btn.png")
        val easyBtnDrawable = Drawable.createFromStream(easyBtnStream, null)
        easyBtn.background = easyBtnDrawable

        // MEDIUM BACKGROUND
        val mediumBtn = findViewById<Button>(R.id.mediumButton)
        val mediumBtnStream = assets.open("Backgrounds/medium_diff_background_btn.png")
        val mediumBtnDrawable = Drawable.createFromStream(mediumBtnStream, null)
        mediumBtn.background = mediumBtnDrawable

        // HELL BACKGROUND
        val hellBtn = findViewById<Button>(R.id.hardButton)
        val hellBtnStream = assets.open("Backgrounds/hard_diff_background_btn.png")
        val hellBtnDrawable = Drawable.createFromStream(hellBtnStream, null)
        hellBtn.background = hellBtnDrawable

        fadeIn(diffContainer)
        diffContainer.visibility = View.VISIBLE
    }

    private fun cargarPartida(){
        val intent = Intent(this, PartidaActivity::class.java).apply {
            putExtra("cargarDatos", true)
        }

        // DETENEMOS LA MUSICA DE FONDO
        val musicIntent = Intent(this, InitialMusicService::class.java)
        stopService(musicIntent)

        startActivity(intent)
    }

    private fun background(background: View){
        val diffContainer = findViewById<CardView>(R.id.difficultyContainer)
        if (diffContainer.visibility == View.VISIBLE) {
            fadeOut(diffContainer)
            diffContainer.visibility = View.GONE
        }
    }

    private fun launchGame(difficulty: Int){
        val intent = Intent(this, PartidaActivity::class.java).apply {
            putExtra("DIFFICULTY_LEVEL", difficulty)
        }

        // DETENEMOS LA MUSICA DE FONDO
        val musicIntent = Intent(this, InitialMusicService::class.java)
        stopService(musicIntent)

        startActivity(intent)
    }
}
