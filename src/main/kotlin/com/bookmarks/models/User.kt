package com.bookmarks.models

data class User(
    val id: UInt,
    val name: String,
    val surname: String,
    val nickname: String,
) {
    val subscriptions get(): Set<Subscription> = mySubscriptions
    val purchasedBookIds get(): Set<UInt> = myPurchasedBookIds

    private val mySubscriptions = mutableSetOf<Subscription>()
    private val myPurchasedBookIds = mutableSetOf<UInt>()

    fun purchase(bookId: UInt) {
        myPurchasedBookIds += bookId
    }

    fun subscribe(subscription: Subscription) {
        mySubscriptions += subscription
    }
}
