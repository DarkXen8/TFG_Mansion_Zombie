package com.example.tfg_mansionzombie

class Jugador(
    var vida: Int = 100,
    val daño: Int = 15,
    var protecciones: Int = 0,
    var curaciones: Boolean = false,
    val armas: Int = 0
){
    fun recibirDaño(dañoZ: Int){
        vida -= dañoZ
        if (vida < 0 ){
            vida = 0
        }
    }

    fun atacar(zombie: Zombie){
        zombie.recibirDaño(daño)
    }

    fun curarse(){
        if (curaciones){
            vida += 50
            curaciones = false
        }
    }
}