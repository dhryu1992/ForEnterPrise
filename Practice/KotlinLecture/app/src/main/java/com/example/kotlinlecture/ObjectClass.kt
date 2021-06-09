package com.example.kotlinlecture

// Singletone Pattern
// 만들어진 객체는 단 하나. 그 하나로 다 씀
object CarFactory {
    val cars = mutableListOf<Car>()

    fun makeCar(housePower: Int) : Car {
        val car = Car(housePower)
        cars.add(car)
        return car
    }
}

data class Car(val horsePower : Int)

fun main() {
    val car = CarFactory.makeCar(10)
    val car2 = CarFactory.makeCar(20)

    println(car)
    println(car2)
    println(CarFactory.cars.size.toString())

}