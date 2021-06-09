package com.example.kotlinlecture

class Book private constructor(val id: Int, val name: String) {

    companion object BookFactory : IdProvider{

        override fun getId(): Int {
            return 3
        }

        val myBook = "new book"

        // companion object = private property나 메소드를 읽어올 수 있게 함.
        fun create() = Book(getId(), myBook)
    }

}

interface IdProvider {
    fun getId(): Int
}

fun main() {
    val book = Book.create()

    val bookId = Book.BookFactory.getId()

    println("${book.id}, ${book.name}")
    println("${bookId}")
}