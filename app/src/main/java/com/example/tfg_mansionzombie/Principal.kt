package com.example.tfg_mansionzombie

import android.annotation.SuppressLint
import android.content.Intent
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
        val background = findViewById<ImageView>(R.id.imageView4)

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

        }
        mediobtn.setOnClickListener {

        }
        dificilbtn.setOnClickListener {

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
        val diffContainer = findViewById<CardView>(R.id.difficultyContainer)
        fadeIn(diffContainer)
        diffContainer.visibility = View.VISIBLE
    }

    private fun background(background: View){
        val diffContainer = findViewById<CardView>(R.id.difficultyContainer)
        if (diffContainer.visibility == View.VISIBLE) {
            fadeOut(diffContainer)
            diffContainer.visibility = View.GONE
        }
    }

    private fun launchGame(difficulty: View){
        val intent = Intent(this, PartidaActivity::class.java).apply {
            // putExtra("DIFFICULTY_LEVEL", difficulty)
        }
        startActivity(intent)
    }
}
