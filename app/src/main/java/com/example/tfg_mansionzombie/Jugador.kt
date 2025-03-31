package com.example.tfg_mansionzombie

class Jugador(
    var vida: Int = 100,
    val daño: Int = 15,
    var protecciones: Int = 0,
    var curaciones: Boolean = false,
    var armas: Int = 0,
    var busquedas: Int = 3
){
    fun recibirDaño(dañoZ: Int) {
        val dañoReal = dañoZ - (protecciones * 5)

        if (dañoReal > 0) {
            vida -= dañoReal
            if (vida < 0) {
                vida = 0
            }
        }
        // Si dañoReal <= 0, no se aplica ningún daño
    }

    fun atacar(zombie: Zombie){
        zombie.recibirDaño((daño + (armas * 10)))
    }

    fun curarse(){
        val healRandom = (1..3).random()

        if (healRandom == 1){
            vida += 25
        }
        if (healRandom == 2){
            vida += 50
        }
        if (healRandom == 3){
            vida += 75
        }
        if (vida > 100){
            vida = 100
        }
    }
}