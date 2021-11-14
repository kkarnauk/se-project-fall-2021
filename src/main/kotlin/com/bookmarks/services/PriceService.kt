package com.bookmarks.services

import com.bookmarks.models.*
import org.springframework.stereotype.Component

@Component
class PriceService {
    fun calculatePurchasePrice(user: User, book: Book): Price? {
        if (book.id in user.purchasedBookIds) {
            return null
        }
        return user.subscriptions
            .onEach { if (it.bookIncluded(book)) return null }
            .map { book.basePrice * it.discount }
            .minOrNull() ?: book.basePrice
    }

    fun calculatePurchasePrice(user: User, books: List<Book>): Price? = books
        .ifEmpty { return null }
        .map { calculatePurchasePrice(user, it) }
        .ifNullsIn { return null }
        .sumOf { it.toCents() }
        .fromCents() * when {
        books.size >= 10 -> 0.8
        books.size >= 5 -> 0.9
        else -> 1.0
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <T> List<T?>.ifNullsIn(ifInDo: () -> List<T>): List<T> {
        return if (null in this) ifInDo() else this as List<T>
    }
}
