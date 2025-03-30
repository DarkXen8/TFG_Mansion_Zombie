package com.example.tfg_mansionzombie

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.colorResource
import org.w3c.dom.Text


class PartidaActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partida)

        val difficulty = intent.getIntExtra("DIFFICULTY_LEVEL", 1)
        val difficultyText = findViewById<TextView>(R.id.DifficultySelected)

        if (difficulty == 1){
            difficultyText.text = "Dificultad: Easy"
            difficultyText.setTextColor(Color.GREEN)
        }
        if (difficulty == 2){
            difficultyText.text = "Dificultad: Medium"
            difficultyText.setTextColor(Color.YELLOW)
        }
        if (difficulty == 3){
            difficultyText.text = "Dificultad: Hell"
            difficultyText.setTextColor(Color.RED)
        }

        var jugador = Jugador()
        var jugadorHP = findViewById<TextView>(R.id.playerHP)

        jugadorHP.text = jugador.vida.toString() + "/100"
    }
}