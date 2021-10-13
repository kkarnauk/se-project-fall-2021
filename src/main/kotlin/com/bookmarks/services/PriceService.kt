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
        throw UnsupportedOperationException()
    }
}