package com.bookmarks.models

sealed class Subscription(val discount: Double) {
    abstract fun bookIncluded(book: Book): Boolean

    object YandexPlus : Subscription(0.5) {
        override fun bookIncluded(book: Book) = book.id in 1u..200u
    }

    object Bookmark : Subscription(0.8) {
        // TODO create database (assignee @psurkov)
        override fun bookIncluded(book: Book) = book.id in 1u..400u
    }

    class ForAuthor(private val author: Author) : Subscription(1.0) {
        override fun bookIncluded(book: Book): Boolean = author.id == book.authorId
    }

    object ForChildren : Subscription(1.9) {
        override fun bookIncluded(book: Book): Boolean = false
    }
}
