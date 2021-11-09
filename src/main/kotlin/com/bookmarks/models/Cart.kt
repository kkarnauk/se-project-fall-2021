package com.bookmarks.models

data class Cart(val elements: List<Element>)

sealed class Element(val book: Book)
class Purchase(book: Book) : Element(book)
class Rent(book: Book, val weeks: Int) : Element(book)