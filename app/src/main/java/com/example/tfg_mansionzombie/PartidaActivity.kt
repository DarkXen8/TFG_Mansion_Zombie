package com.example.tfg_mansionzombie

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity


class PartidaActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    private lateinit var enemigo: Zombie
    private var prevZombie: Int = 0

    private lateinit var jugador: Jugador

    private var enemigoMaxHP: Int = 0

    private lateinit var initialBackground: ImageView
    private var prevRoom: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partida)

        initialBackground = findViewById(R.id.initialBackground)
        val inputStream = assets.open("Backgrounds/room_1.png")
        val drawable = Drawable.createFromStream(inputStream, null)
        initialBackground.setImageDrawable(drawable)


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

        jugador = Jugador()
        var jugadorHP = findViewById<TextView>(R.id.playerHPInsideBar)

        jugadorHP.text = jugador.vida.toString() + "/100"

        var enemigoMaxHP = 0

        // ESTADO INICIAL DE LA PANTALLA
        val enemySprite = findViewById<ImageView>(R.id.EnemySprite)
        val enemyHPBar = findViewById<ProgressBar>(R.id.EnemyHP_Bar)
        val enemyHPText = findViewById<TextView>(R.id.EnemyHP)
        val enemyHP = findViewById<TextView>(R.id.enemyHPInsideBar)
        val atacarBtn = findViewById<Button>(R.id.attack_btn)
        val curarBtn = findViewById<Button>(R.id.heal_btn)
        val buscarBtn = findViewById<Button>(R.id.search_btn)
        val avanzarBtn = findViewById<Button>(R.id.next_btn)

        enemySprite.visibility = View.GONE
        enemyHPBar.visibility = View.GONE
        enemyHPText.visibility = View.GONE
        enemyHP.visibility = View.GONE
        atacarBtn.isEnabled = false
        curarBtn.isEnabled = false
        buscarBtn.isEnabled = false
        avanzarBtn.isEnabled = true


        // LLAMADAS DE LOS BOTONES AL SER PULSADOS
        atacarBtn.setOnClickListener {
            atacar(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP)
        }
        curarBtn.setOnClickListener {
            curar()
        }
        buscarBtn.setOnClickListener {
            buscar()
        }
        avanzarBtn.setOnClickListener {
            avanzar(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP)
        }
    }

    @SuppressLint("SetTextI18n")
    fun atacar(
        enemySprite: ImageView,
        enemyHPBar: ProgressBar,
        enemyHPText: TextView,
        atacarBtn: Button,
        curarBtn: Button,
        buscarBtn: Button,
        avanzarBtn: Button,
        enemyHP: TextView
    ) {
        jugador.atacar(enemigo)

        if (enemigo.vida == 0){
            enemySprite.visibility = View.GONE
            enemyHPBar.visibility = View.GONE
            enemyHPText.visibility = View.GONE
            enemyHP.visibility = View.GONE
            atacarBtn.isEnabled = false
            curarBtn.isEnabled = true
            buscarBtn.isEnabled = true
            avanzarBtn.isEnabled = true
        }else{
            enemyHP.text = enemigo.vida.toString() + "/" + enemigoMaxHP
            enemyHPBar.progress = enemigo.vida
        }
    }

    fun curar(){

    }

    fun buscar(){

    }

    @SuppressLint("SetTextI18n")
    fun avanzar(
        enemySprite: ImageView,
        enemyHPBar: ProgressBar,
        enemyHPText: TextView,
        atacarBtn: Button,
        curarBtn: Button,
        buscarBtn: Button,
        avanzarBtn: Button,
        enemyHP: TextView
    ) {
        enemigo = Zombie()

        zombieRandomizer(enemySprite)
        enemySprite.visibility = View.VISIBLE
        enemyHPBar.visibility = View.VISIBLE
        enemyHPText.visibility = View.VISIBLE
        enemyHP.visibility = View.VISIBLE

        enemigoMaxHP = enemigo.vida

        enemyHP.text = enemigo.vida.toString() + "/" + enemigoMaxHP
        enemyHPBar.max = enemigoMaxHP
        enemyHPBar.progress = enemigoMaxHP

        atacarBtn.isEnabled = true
        curarBtn.isEnabled = true
        buscarBtn.isEnabled = false
        avanzarBtn.isEnabled = false
        backgroundRandomizer()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun backgroundRandomizer() {
        var nuevoRoom: Int

        // Forzar que el número aleatorio no sea igual al anterior
        do {
            nuevoRoom = (1..5).random()
        } while (nuevoRoom == prevRoom)

        val ruta = when (nuevoRoom) {
            1 -> "Backgrounds/room_1.png"
            2 -> "Backgrounds/room_2.png"
            3 -> "Backgrounds/room_3.png"
            4 -> "Backgrounds/room_4.png"
            5 -> "Backgrounds/room_5.png"
            else -> "Backgrounds/room_1.png"
        }

        try {
            val inputStream = assets.open(ruta)
            val drawable = Drawable.createFromStream(inputStream, null)
            initialBackground.setImageDrawable(drawable)
            prevRoom = nuevoRoom // ✅ actualizar la sala actual
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun zombieRandomizer(enemySprite: ImageView) {
        var nuevoZombie: Int

        do {
            nuevoZombie = (1..4).random() // Cambia el rango si tienes más o menos
        } while (nuevoZombie == prevZombie)

        val ruta = when (nuevoZombie) {
            1 -> "EnemySprites/enemy_1.png"
            2 -> "EnemySprites/enemy_2.png"
            3 -> "EnemySprites/enemy_3.png"
            4 -> "EnemySprites/enemy_4.png"
            else -> "EnemySprites/enemy_1.png"
        }

        try {
            val inputStream = assets.open(ruta)
            val drawable = Drawable.createFromStream(inputStream, null)
            enemySprite.setImageDrawable(drawable)
            prevZombie = nuevoZombie // ✅ actualizamos el zombi actual
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}