package com.bookmarks.models

data class Book(val id: UInt, val name: String, val basePrice: Price, val authorId: String)