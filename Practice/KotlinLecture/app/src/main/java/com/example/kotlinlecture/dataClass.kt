package com.example.kotlinlecture

data class Ticket(val companyName: String, val name: String, var date: String, var seatNumber: Int)
//toString(), hashCode(), equals(), copy()

data class TicketNormal(val companyName: String, val name: String, var date: String, var seatNumber: Int)

fun main() {
    val ticketA = Ticket("KoreanAir", "shu", "2020.2.16", 14)
    val ticketB = TicketNormal("Asiana", "Song", "2020.6.2", 15)

    println(ticketA)
    println(ticketB)
}