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
    private var jugadorMaxHP: Int = 0

    private lateinit var initialBackground: ImageView
    private var prevRoom: Int = 1
    private var maxRoom: Int = 0
    private var actualRoom: Int = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partida)

        initialBackground = findViewById(R.id.initialBackground)
        val inputStream = assets.open("Backgrounds/room_1.png")
        val drawable = Drawable.createFromStream(inputStream, null)
        initialBackground.setImageDrawable(drawable)


        val difficulty = intent.getIntExtra("DIFFICULTY_LEVEL", 1)
        val difficultyText = findViewById<TextView>(R.id.DifficultySelected)
        val roomNumberText = findViewById<TextView>(R.id.RoomNumber)


        if (difficulty == 1){
            difficultyText.text = "Dificultad: Easy"
            difficultyText.setTextColor(Color.GREEN)
            maxRoom = 5
        }
        if (difficulty == 2){
            difficultyText.text = "Dificultad: Medium"
            difficultyText.setTextColor(Color.YELLOW)
            maxRoom = 10
        }
        if (difficulty == 3){
            difficultyText.text = "Dificultad: Hell"
            difficultyText.setTextColor(Color.RED)
            maxRoom = 15
        }

        roomNumberText.text = actualRoom.toString() + "/" + maxRoom.toString()

        jugador = Jugador()
        jugadorMaxHP = jugador.vida
        var playerHP = findViewById<TextView>(R.id.playerHPInsideBar)
        var playerHPBar = findViewById<ProgressBar>(R.id.PlayerHP_Bar)

        playerHP.text = jugador.vida.toString() + "/" + jugadorMaxHP

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
            atacar(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP, playerHP, playerHPBar)
        }
        curarBtn.setOnClickListener {
            curar(curarBtn, playerHPBar, playerHP)
        }
        buscarBtn.setOnClickListener {
            buscar(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP)
        }
        avanzarBtn.setOnClickListener {
            avanzar(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP, roomNumberText)
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
        enemyHP: TextView,
        playerHP: TextView,
        playerHPBar: ProgressBar
    ) {
        jugador.atacar(enemigo)

        if (enemigo.vida == 0){
            enemySprite.visibility = View.GONE
            enemyHPBar.visibility = View.GONE
            enemyHPText.visibility = View.GONE
            enemyHP.visibility = View.GONE
            atacarBtn.isEnabled = false
            if (jugador.curaciones){
                curarBtn.isEnabled = true
            }
            if (jugador.busquedas != 0){
                buscarBtn.isEnabled = true
            }
            avanzarBtn.isEnabled = true
        }else{
            enemyHP.text = enemigo.vida.toString() + "/" + enemigoMaxHP
            enemyHPBar.progress = enemigo.vida
            enemigo.atacar(jugador)
            playerHP.text = jugador.vida.toString() + "/" + jugadorMaxHP
            playerHPBar.progress = jugador.vida
            if (jugador.vida == 0){
                atacarBtn.isEnabled = false
                curarBtn.isEnabled = false
                buscarBtn.isEnabled = false
                avanzarBtn.isEnabled = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun curar(curarBtn: Button, playerHPBar: ProgressBar, playerHP: TextView) {
        jugador.curarse()
        curarBtn.isEnabled = false
        playerHPBar.progress = jugador.vida
        playerHP.text = jugador.vida.toString() + "/" + jugadorMaxHP
        jugador.curaciones = false
    }

    fun buscar(
        enemySprite: ImageView,
        enemyHPBar: ProgressBar,
        enemyHPText: TextView,
        atacarBtn: Button,
        curarBtn: Button,
        buscarBtn: Button,
        avanzarBtn: Button,
        enemyHP: TextView
    ) {
        val searchRandom = (1..4).random()

        if (searchRandom == 1){
            jugador.curaciones = true
            curarBtn.isEnabled = true
        }
        if ( searchRandom == 2){
            jugador.armas += 1
        }
        if (searchRandom == 3){
            jugador.protecciones += 1
        }
        if (searchRandom == 4){
            spawnZombie(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP)
        }
        jugador.busquedas -= 1
        if (jugador.busquedas == 0){
            buscarBtn.isEnabled = false
        }
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
        enemyHP: TextView,
        roomNumberText: TextView
    ) {
        if (actualRoom != maxRoom){
            spawnZombie(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP)
            backgroundRandomizer()
            jugador.busquedas = 3
            actualRoom += 1
            roomNumberText.text = actualRoom.toString() + "/" + maxRoom.toString()
        }
        // IMPLEMENTAR LA VENTANA DE VICTORIA
    }

    @SuppressLint("SetTextI18n")
    fun spawnZombie(
        enemySprite: ImageView,
        enemyHPBar: ProgressBar,
        enemyHPText: TextView,
        atacarBtn: Button,
        curarBtn: Button,
        buscarBtn: Button,
        avanzarBtn: Button,
        enemyHP: TextView
    ){
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
        if (jugador.curaciones){
            curarBtn.isEnabled = true
        }
        buscarBtn.isEnabled = false
        avanzarBtn.isEnabled = false
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