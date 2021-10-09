package com.bookmarks.models

data class Price(val dollars: Int, val cents: Int) {
    operator fun compareTo(other: Price): Int =
        (dollars * 100 + cents).compareTo(other.dollars + other.cents)
}