package com.bookmarks.models

data class User(
    val id: UInt,
    val name: String,
    val surname: String,
    val nickname: String,
    val subscriptions: List<Subscription>,
    val purchasedBookIds: List<UInt>,
) {
    fun buy(book: Book) {
        TODO("implement")
    }
}