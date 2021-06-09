package com.example.kotlinlecture

fun main() {
    ignoreNull("shu")
}

// 4. 조건식

fun maxBy(a: Int, b: Int) = if(a > b) a else b

fun checkNum(Score: Int) {
    when(Score) {
        0 -> println("this is 0")
        1 -> println("this is 1")
        2, 3 -> println("this is 2 or 3")
        else -> println("I Don't know")
        // 변수에 들어가지 않는건 return 하는게 아니어서 else 없어도 문제 발생 X but,
    }

    var b = when(Score) {
        1 -> 1
        2 -> 2
        else -> 3
        // 변수에 들어갈땐 return을 해야하기에 else 무조건 붙혀줘야
    }

    println("b: ${b}")

    when(Score) {
        in 90..100 -> println("You're Genius")
        in 10..80 -> println("not bad")
        else -> println("okay")
    }
}
// Expression vs Statement
// 코틀린 모든 함수는 Exprssion -> 리턴값이 없는게 아니라 Unit 로 반환.

// 6. Array and List
// Array

// List 1. List(수정불가) 2. MutableList(수정가능)

fun array() {
    // arrayof, listof -> 초기화 과정
    val array = arrayOf(1, 2, 3)
    val list = listOf(1, 2, 3)

    val array2 = arrayOf(1, "d", 3, 4f)
    val list2 = listOf(1, "d", 11L)

    array[0] = 3 // 가능
    var result = list.get(0)

    var arrayList = arrayListOf<Int>()
    arrayList.add(10)
    arrayList.add(20)

    val mulist1 : MutableList<Int> = mutableListOf(10, 20, 30, 40, 50)
    val mulist2 = mutableListOf(10, 20, 30)
    val mulist3 =(1..50).toMutableList()
    val mulist4 = mutableListOf<Int>()
    val mulist5 = MutableList<Int>(5, { i -> i}) //0..4로 초기화
}

//7. 반복문(for, while)
fun forAndWhile() {
    val students = arrayListOf("shu", "god", "her", "jennifer")

    for(name in students) {
        // in 연산자를 사용한 반복조건자. 있는 수만큼 반복.
            // name -> 변수(여기서 이렇게 선언가능한듯)
        println("${name}")
    }

    for((index, name) in students.withIndex()) {
        println("${index + 1}번째 학생: ${name}")
    }

    var sum: Int = 0
    for(i in 1 until 10) {
        //1부터 10까지 한번 돌려보란뜻.
        sum += i
    }
    println(sum)

    var index = 0
    while(index < 10) {
        println("current index: ${index}")
        index++
    }
}

// 7. Nullable/NonNull

fun nullcheck() {
    //NPE: Null Point Exception

    var name = "shu" // Null이면 안되는 타입

    var nullName : String? = null // NonNull 타입에 null을 넣어서 에러발생 -> 자료형에 ? 붙히면 Nullable 타입이 됨. 그때부터 null 대입 가능.

    var nameInUpperCase = name.toUpperCase() // name같은 경우는 NonNull 이기 때문에 바로 nameInupperCase 함수 사용 가능.

    var nullNameInUpperCase = nullName?.toUpperCase() // ? 붙혔을 때 -> nullName이 null이 아니면 메서드실행, but null이면 null 반환

    println("name: ${nameInUpperCase} nullName : ${nullNameInUpperCase}")

    //?:

    val lastName : String? = null

    val fullName = name +" "+(lastName?: "No lastName") //lastName이 있으면(!= null) lastName 출력, lastname이 없으면 "No lastName"을 출력하라.
    println("fullname: ${fullName}")



}

//!!: 컴파일러에게 null이 아니라는걸 말해준다. 확실하지 않은 이상, 쓰면안됨.
fun ignoreNull(str: String?) {
//    val mNotNull : String = null!!
//    val upper = mNotNull.toUpperCase()

    val email : String = "shu2012@naver.com"
    email?.let {
        // email이 null이 아니면 이런걸 해라!
        // let 함수는 자신의 객체(여기서는 email)을 람다식 내부로 옮겨준다.
        println("my email is ${email}")
    }
}



//1, 함수

fun helloWorld() : Unit {
    println("Hello World")
}

fun add(a : Int, b: Int) : Int {
    return a + b
}

//2. val(value) - 불변 , var(variable) - 가변

fun hi() {
    val a : Int = 10
    var b : Int = 9

    // 변수 선언
    var e : String

    b = 100
    println(b)

    val c = 100
    var d = 100

    var name = "Shu"
}


// 사용하지 않으면 회색으로 변수명이 뜬다.
// Redundant : 쓸 필요 없을 때 검은색 글자로 오류표시
// return 형식이 없을땐 Unit
// 무언가를 return 할때에는 무조건 반환형을 써줘야함
// 뭘 return 하든 fun을 앞에 쓴다.