package com.example.kotlinlecture

// 1. Lambda
// 람다식은 우리가 마치 Value 처럼 다룰 수 있는 익명함수이다.
// 1) 메소드의 parameter로 넘겨줄 수 있다. fun maxBy(function)
// 2) return 값으로 사용할 수 있다.

// 람다의 기본정의
// val lambdaName : Type = {argumentList -> codeBody}

val square = {number: Int -> number*number}

val nameAge = {name: String, age: Int ->
    "myName is ${name}, I'm ${age}"
}


fun main() {
    //println(square(12))
    //println(nameAge("shu", 42))
//    val a = "joyce said "
//    val b = "mac said "
//    println((a.pizzaIsGreat()))
//    println((b.pizzaIsGreat()))

//    println(extendString("shu", 30))
//    println(calculateGrade(1000))

    val lambda1 = {number: Double ->
        number == 4.3213
    }

    println(invokeLambda(lambda1))
    println(invokeLambda( {it > 3.2}) )


}


// 확장함수

val pizzaIsGreat: String.() -> String = {
    this + "Pizza is the best"
}

fun extendString(name: String, age: Int) : String {
    val introduceMyself : String.(Int) -> String = {"I am ${this} and ${it} years old"}
    return name.introduceMyself(age)
}

// lambda return
val calculateGrade : (Int) -> String = {
    when(it) {
        in 0..40 -> "fail"
        in 41..70 -> "pass"
        in 71..100 -> "perfect"
        else -> "Error"
    }
}

// 람다를 표현하는 방법

fun invokeLambda(lambda: (Double) -> Boolean) : Boolean {
    return lambda(5.2343)
}

// pojo class(모델이 되는 클래스)