package com.bookmarks.services

import com.bookmarks.models.Book
import com.bookmarks.models.Price
import com.bookmarks.models.Price.Companion.fromCents
import com.bookmarks.models.Subscription
import com.bookmarks.models.User
import org.springframework.stereotype.Component

@Component
object PriceService {
    fun calculatePurchasePrice(user: User, book: Book): Price? { // TODO: керил сделай красиво
        if (user.purchasedBookIds.isNotEmpty()) {
            return null
        }
        return user.subscriptions.map {
            book.basePrice * it.discount
        }.map { it.cents }.minOrNull()?.fromCents() ?: book.basePrice
    }
}