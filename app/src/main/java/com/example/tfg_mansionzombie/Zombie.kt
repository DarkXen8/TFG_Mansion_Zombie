package com.example.tfg_mansionzombie

class Zombie(
    var vida: Int = 25,
    val daño: Int = 10
){

    fun recibirDaño(dañoJ: Int){
        vida -= dañoJ
        if (vida < 0 ){
            vida = 0
        }
    }

    fun atacar(jugador: Jugador){
        jugador.recibirDaño(daño)
    }
}