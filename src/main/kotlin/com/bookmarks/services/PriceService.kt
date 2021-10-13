package com.bookmarks.services

import com.bookmarks.models.Book
import com.bookmarks.models.Price
import com.bookmarks.models.Price.Companion.fromCents
import com.bookmarks.models.Price.Companion.toCents
import com.bookmarks.models.Subscription
import com.bookmarks.models.User
import org.springframework.stereotype.Component

@Component
object PriceService {
    fun calculatePurchasePrice(user: User, book: Book): Price? { // TODO: керил сделай красиво
        if (book.id in user.purchasedBookIds) {
            return null
        }
//        if (user.subscriptions.contains(Subscription.YandexPlus)) {
//            return (2099 / 2).fromCents()
//        }
        return user.subscriptions.map {
            book.basePrice * it.discount
        }.map { it.toCents() }.minOrNull()?.fromCents() ?: book.basePrice
    }

    fun calculatePurchasePrice(user: User, books: List<Book>): Price? {
        return books
            .map { calculatePurchasePrice(user, it) }
            .ifNullsIn { return null }
            ?.sumOf { it.toCents() }
            ?.fromCents()
    }

    private inline fun <T> List<T?>.ifNullsIn(ifInDo : () -> Unit): List<T>? {
        if (null in this) {
            ifInDo()
            return null
        }
        return filterNotNull()
    }
}