package com.bookmarks.services

import com.bookmarks.models.Book
import com.bookmarks.models.Price
import com.bookmarks.models.Price.Companion.fromCents
import com.bookmarks.models.Price.Companion.toCents
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

    fun calculatePurchasePrice(user: User, books: List<Book>): Price? =
        books
        .map { calculatePurchasePrice(user, it) }
        .ifNullsReturn { return null }
        .sumOf { it.toCents() }
        .fromCents() * when {
            books.size >= 10 -> 0.8
            books.size >= 5 -> 0.9
            else -> 1.0
        }

    @Suppress("UNCHECKED_CAST")
    private inline fun <T> List<T?>.ifNullsReturn(ifInDo: () -> Nothing): List<T> {
        if (null in this) ifInDo()
        return this as List<T>
    }
}