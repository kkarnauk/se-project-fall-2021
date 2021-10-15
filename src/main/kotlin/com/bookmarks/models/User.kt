package com.bookmarks.models

data class User(
    val id: UInt,
    val name: String,
    val surname: String,
    val nickname: String,
    val subscriptions: MutableSet<Subscription>,
    val purchasedBookIds: MutableSet<UInt>,
) {
    fun purchase(book: Book) {
        purchasedBookIds += book.id
    }
}