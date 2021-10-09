package com.bookmarks.models

data class Price(val dollars: Int, val cents: Int) {
    operator fun compareTo(other: Price): Int =
        toCents().compareTo(other.toCents())

    operator fun times(discount: Double): Price = fromCents((toCents() * discount).toInt())


    companion object {
        fun Price.toCents(): Int = dollars * 100 + cents
        fun fromCents(cents: Int): Price = Price(cents / 100, cents % 100)
    }
}