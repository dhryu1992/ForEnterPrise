package com.example.kotlinlecture

open class Human (val _name: String = "Anonymous") {

    constructor(name: String, age: Int): this(name) {
        println("my name is ${name}, ${age} years old")
    }



    init {
        println("New Human has been born!")
    }

    fun eatingCake() {
        println("This is so YUMMY~~~")
    }

    open fun singASong() {
        println("lalala")
    }
}

class Korean: Human() {

    override fun singASong() {
        super.singASong()
        println("라라라")
        println("my name is : ${_name}")
    }
}

fun main() {
//    val human = Human()
//    human.eatingCake()
//
//    println("this human's name is ${human.name}")
//    val mom = Human("Kuri", 22)
    val korean = Korean()
    korean.singASong()
}