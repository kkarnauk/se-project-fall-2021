package com.bookmarks.models

data class Price(val dollars: Int, val cents: Int) : Comparable<Price> {
    override operator fun compareTo(other: Price): Int =
        toCents().compareTo(other.toCents())

    operator fun times(discount: Double): Price = ((toCents() * discount).toInt().fromCents())


    companion object {
        fun Price.toCents(): Int = dollars * 100 + cents
        fun Int.fromCents(): Price = Price(this / 100, this % 100)
    }
}