package com.example.tfg_mansionzombie

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

    private var actualSprite: Int = 0
    private var difficulty: Int = 0

    private var fail: Boolean = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partida)

        val musicIntent = Intent(this, GameMusicService::class.java)
        startService(musicIntent)

        initialBackground = findViewById(R.id.initialBackground)
        val inputStream = assets.open("Backgrounds/room_1.png")
        val drawable = Drawable.createFromStream(inputStream, null)
        initialBackground.setImageDrawable(drawable)

        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val saveBtnStream = assets.open("Backgrounds/saveBtnIcon.png")
        val saveBtnDrawable = Drawable.createFromStream(saveBtnStream, null)
        saveBtn.background = saveBtnDrawable

        difficulty = intent.getIntExtra("DIFFICULTY_LEVEL", 1)
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
        atacarBtn.alpha = 0.5f
        curarBtn.isEnabled = false
        curarBtn.alpha = 0.5f
        buscarBtn.isEnabled = false
        buscarBtn.alpha = 0.5f
        avanzarBtn.isEnabled = true
        avanzarBtn.alpha = 1.0f


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
            avanzar(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP, roomNumberText, musicIntent)
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
        atacarBtn.isEnabled = false
        atacarBtn.alpha = 0.5f
        curarBtn.isEnabled = false
        curarBtn.alpha = 0.5f

        lifecycleScope.launch {
            // 1. Ataca el jugador
            jugador.atacar(enemigo)

            // 2. Mostrar daño hecho al zombi
            val dmgJugador = jugador.daño + (jugador.armas * 10)
            val dmgTxt = findViewById<TextView>(R.id.playerDmg)
            dmgTxt.text = "-$dmgJugador"
            dmgTxt.translationY = 0f
            dmgTxt.alpha = 1f
            dmgTxt.visibility = View.VISIBLE

            dmgTxt.animate()
                .translationY(50f)
                .alpha(0f)
                .setDuration(1000)
                .withEndAction { dmgTxt.visibility = View.GONE }
                .start()

            // 3. Cambiar sprite a dañado
            val dmgDrawable = withContext(Dispatchers.IO) {
                assets.open("EnemySprites/dmg_enemy_$actualSprite.png").use {
                    Drawable.createFromStream(it, null)
                }
            }
            enemySprite.setImageDrawable(dmgDrawable)

            // 4. Actualizar vida del enemigo inmediatamente
            enemyHP.text = "${enemigo.vida}/${enemigoMaxHP}"
            enemyHPBar.progress = enemigo.vida


            if (enemigo.vida == 0) {
                atacarBtn.isEnabled = false
                atacarBtn.alpha = 0.5f
                curarBtn.isEnabled = false
                curarBtn.alpha = 0.5f
                buscarBtn.isEnabled = false
                buscarBtn.alpha = 0.5f
                avanzarBtn.isEnabled = false
                avanzarBtn.alpha = 0.5f

                enemySprite.animate()
                    .translationY(50f)
                    .alpha(0f)
                    .setDuration(1000)
                    .withStartAction {
                        enemyHPBar.animate().alpha(0f).setDuration(1000).start()
                        enemyHP.animate().alpha(0f).setDuration(1000).start()
                        enemyHPText.animate().alpha(0f).setDuration(1000).start()
                    }
                    .withEndAction {
                        enemySprite.visibility = View.GONE
                        enemySprite.alpha = 1f
                        enemySprite.translationY = 0f

                        enemyHPBar.visibility = View.GONE
                        enemyHPText.visibility = View.GONE
                        enemyHP.visibility = View.GONE
                        enemyHPBar.alpha = 1f
                        enemyHP.alpha = 1f
                        enemyHPText.alpha = 1f

                        avanzarBtn.isEnabled = true
                        avanzarBtn.alpha = 1.0f
                        if (jugador.curaciones) {
                            curarBtn.isEnabled = true
                            curarBtn.alpha = 1.0f
                        }
                        if (jugador.busquedas > 0) {
                            buscarBtn.isEnabled = true
                            buscarBtn.alpha = 1.0f
                        }
                    }
                    .start()

                return@launch // ✅ ¡Evita continuar el combate!
            }



            // 5. Esperar 1 segundo y volver al sprite normal (solo si el enemigo no murió)
            delay(1000)
            val normalDrawable = withContext(Dispatchers.IO) {
                assets.open("EnemySprites/enemy_$actualSprite.png").use {
                    Drawable.createFromStream(it, null)
                }
            }
            enemySprite.setImageDrawable(normalDrawable)

            // 6. El enemigo ataca al jugador
            enemigo.atacar(jugador)

            // 7. Pantalla roja de daño
            val screenOverlay = findViewById<View>(R.id.redOverlay)
            screenOverlay.alpha = 0.6f
            screenOverlay.visibility = View.VISIBLE
            screenOverlay.animate().alpha(0f).setDuration(1000).withEndAction {
                screenOverlay.visibility = View.GONE
            }

            // 8. Mostrar daño recibido
            val enemyDmg = findViewById<TextView>(R.id.enemyDmg)
            enemyDmg.text = "-${enemigo.daño}"
            enemyDmg.translationY = 0f
            enemyDmg.alpha = 1f
            enemyDmg.visibility = View.VISIBLE

            enemyDmg.animate()
                .translationY(50f)
                .alpha(0f)
                .setDuration(1000)
                .withEndAction { enemyDmg.visibility = View.GONE }
                .start()

            // 9. Actualizar vida del jugador
            playerHP.text = "${jugador.vida}/${jugadorMaxHP}"
            playerHPBar.progress = jugador.vida

            // 10. Verificar si muere el jugador
            if (jugador.vida == 0) {
                fail = true
                avanzarBtn.isEnabled = true
                avanzarBtn.alpha = 1.0f
                val exitDrawable = withContext(Dispatchers.IO) {
                    assets.open("Backgrounds/exit_button_background.png").use {
                        Drawable.createFromStream(it, null)
                    }
                }
                avanzarBtn.background = exitDrawable
            } else {
                atacarBtn.isEnabled = true
                atacarBtn.alpha = 1.0f
                if (jugador.curaciones) {
                    curarBtn.isEnabled = true
                    curarBtn.alpha = 1.0f
                }
            }
        }
    }




    @SuppressLint("SetTextI18n")
    fun curar(curarBtn: Button, playerHPBar: ProgressBar, playerHP: TextView) {
        jugador.curarse()
        curarBtn.isEnabled = false
        curarBtn.alpha = 0.5f
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
            curarBtn.alpha = 1.0f
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
            buscarBtn.alpha = 0.5f
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
        roomNumberText: TextView,
        musicIntent: Intent
    ) {
        if (fail) {
            // Si el jugador ha perdido
            val intent = Intent(this, VictoriaActivity::class.java).apply {
                putExtra("PLAYER_HEALTH", jugador.vida)
                putExtra("PLAYER_DAMAGE", jugador.daño + (jugador.armas * 10))
                putExtra("MAX_ROOM", actualRoom)
                putExtra("DIFFICULTY_LEVEL", difficulty)
                putExtra("FAIL", true)
            }

            stopService(musicIntent)
            startActivity(intent)
            finish()
            return
        }

        // Si el jugador sigue vivo
        if (actualRoom != maxRoom) {
            spawnZombie(enemySprite, enemyHPBar, enemyHPText, atacarBtn, curarBtn, buscarBtn, avanzarBtn, enemyHP)
            backgroundRandomizer()
            jugador.busquedas = 3
            actualRoom += 1
            roomNumberText.text = "$actualRoom/$maxRoom"

            if (actualRoom == maxRoom) {
                try {
                    val inputStream = assets.open("Backgrounds/exit_button_background.png")
                    val drawable = Drawable.createFromStream(inputStream, null)
                    avanzarBtn.background = drawable
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            // Si llega al final y gana
            val intent = Intent(this, VictoriaActivity::class.java).apply {
                putExtra("PLAYER_HEALTH", jugador.vida)
                putExtra("PLAYER_DAMAGE", jugador.daño + (jugador.armas * 10))
                putExtra("MAX_ROOM", actualRoom)
                putExtra("DIFFICULTY_LEVEL", difficulty)
                putExtra("FAIL", false)
            }

            stopService(musicIntent)
            startActivity(intent)
            finish()
        }
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

        enemyHPBar.alpha = 1f
        enemyHP.alpha = 1f
        enemyHPText.alpha = 1f


        enemigoMaxHP = enemigo.vida

        enemyHP.text = enemigo.vida.toString() + "/" + enemigoMaxHP
        enemyHPBar.max = enemigoMaxHP
        enemyHPBar.progress = enemigoMaxHP

        atacarBtn.isEnabled = true
        atacarBtn.alpha = 1.0f
        if (jugador.curaciones){
            curarBtn.isEnabled = true
            curarBtn.alpha = 1.0f
        }
        buscarBtn.isEnabled = false
        buscarBtn.alpha = 0.5f
        avanzarBtn.isEnabled = false
        avanzarBtn.alpha = 0.5f
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

        actualSprite = when (nuevoZombie){
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            else -> 0
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


    override fun onDestroy() {
        super.onDestroy()

        val musicIntent = Intent(this, GameMusicService::class.java)
        stopService(musicIntent)
    }

    override fun onPause() {
        super.onPause()

        val musicIntent = Intent(this, GameMusicService::class.java)
        
    }
}