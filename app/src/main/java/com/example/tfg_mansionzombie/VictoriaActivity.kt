package com.example.tfg_mansionzombie

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VictoriaActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.victoria)

        // Obtener datos del intent
        val jugadorHP = intent.getIntExtra("PLAYER_HEALTH", 1)
        val jugadorDMG = intent.getIntExtra("PLAYER_DAMAGE", 1)
        val roomMax = intent.getIntExtra("MAX_ROOM", 1)
        val difficulty = intent.getIntExtra("DIFFICULTY_LEVEL", 1)
        val fail = intent.getBooleanExtra("FAIL", false)

        // Referencias a vistas
        val victoryRedTxt = findViewById<TextView>(R.id.victoriaTxtRed)
        val victoryBlackTxt = findViewById<TextView>(R.id.victoriaTxtBlack)
        val statsTxt = findViewById<TextView>(R.id.estadisticasTxt)
        val healthTxt = findViewById<TextView>(R.id.vidaTxt)
        val healthVal = findViewById<TextView>(R.id.vidaVal)
        val dmgTxt = findViewById<TextView>(R.id.danioTxt)
        val dmgVal = findViewById<TextView>(R.id.danioVal)
        val roomTxt = findViewById<TextView>(R.id.salaMaxTxt)
        val roomVal = findViewById<TextView>(R.id.salaMaxVal)
        val diffVal = findViewById<TextView>(R.id.difficulty)
        val doneBtn = findViewById<Button>(R.id.done_btn)

        // Establecer texto de victoria o derrota
        if (fail) {
            victoryRedTxt.text = "DERROTA"
            victoryBlackTxt.text = "DERROTA"
        } else {
            victoryRedTxt.text = "VICTORIA"
            victoryBlackTxt.text = "VICTORIA"
        }

        // Ocultamos todo al inicio
        val allViews = listOf(
            victoryRedTxt, victoryBlackTxt, statsTxt,
            healthTxt, healthVal, dmgTxt, dmgVal,
            roomTxt, roomVal, diffVal, doneBtn
        )
        allViews.forEach { it.visibility = View.INVISIBLE }

        // Mostrar datos con animación
        GlobalScope.launch(Dispatchers.Main) {
            showView(victoryRedTxt)
            showView(victoryBlackTxt)
            showView(statsTxt)

            healthVal.text = "$jugadorHP"
            showView(healthTxt)
            showView(healthVal)

            dmgVal.text = "$jugadorDMG"
            showView(dmgTxt)
            showView(dmgVal)

            roomVal.text = "$roomMax"
            showView(roomTxt)
            showView(roomVal)

            diffVal.text = when (difficulty) {
                1 -> "Dificultad: Fácil"
                2 -> "Dificultad: Medio"
                3 -> "Dificultad: Difícil"
                else -> "Dificultad: -"
            }
            showView(diffVal)

            showView(doneBtn)
        }

        // Botón para volver a la pantalla principal
        doneBtn.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
            finish()
            val musicIntent = Intent(this, InitialMusicService::class.java)
            startService(musicIntent)
        }
    }

    // Mostrar vista con animación y retraso
    private suspend fun showView(view: View) {
        delay(1000) // espera 1 segundo
        view.visibility = View.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 600
            fillAfter = true
        }
        view.startAnimation(fadeIn)
    }
}
