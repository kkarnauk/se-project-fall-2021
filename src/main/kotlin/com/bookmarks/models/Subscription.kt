package com.bookmarks.models

enum class Subscription(val discount: Double) {
    YandexPlus(0.5) {
        override fun bookIncluded(book: Book) = book.id in 1u..200u
    },
    Bookmark(0.8) {
        // TODO create database (assignee @psurkov)
        override fun bookIncluded(book: Book) = book.id in 1u..400u
    };

    abstract fun bookIncluded(book: Book): Boolean
}