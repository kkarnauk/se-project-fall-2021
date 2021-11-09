package com.bookmarks.services

import com.bookmarks.models.Book
import com.bookmarks.models.Price
import com.bookmarks.models.Price.Companion.fromCents
import com.bookmarks.models.Price.Companion.toCents
import com.bookmarks.models.SpecialOffer
import com.bookmarks.models.User
import com.bookmarks.services.PriceService.applySpecialOffer
import java.util.*
import org.springframework.stereotype.Component

@Component
object PriceService {
//    Full rent price if half of purchase price, scales to week linearly
    fun calculateWeekRentPrice(user: User, book: Book, specialOffer: SpecialOffer? = null): Price? =
        if (specialOffer?.freeRentWeekBookIds?.contains(book.id) == true) {
            0.fromCents()
        } else calculatePurchasePriceBase(user, book)?.let {
            (it.toCents() * 7.0 / book.readDays * 0.5).toInt().fromCents().applySpecialOffer(specialOffer)
        }

    fun calculatePurchasePrice(user: User, book: Book, specialOffer: SpecialOffer? = null): Price? =
        calculatePurchasePriceBase(user, book)?.applySpecialOffer(specialOffer)

    private fun calculatePurchasePriceBase(user: User, book: Book): Price? {
        if (book.id in user.purchasedBookIds) {
            return null
        }
        return user.subscriptions
            .onEach { if (it.bookIncluded(book)) return null }
            .map { book.basePrice * it.discount }
            .minOrNull() ?: book.basePrice
    }

    fun calculatePurchasePrice(user: User, books: List<Book>, specialOffer: SpecialOffer? = null): Price? =
        calculatePurchasePriceBase(user, books)?.applySpecialOffer(specialOffer)

    private fun calculatePurchasePriceBase(user: User, books: List<Book>): Price? = books
        .ifEmpty { return null }
        .map { calculatePurchasePriceBase(user, it) }
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

    private fun Price.applySpecialOffer(specialOffer: SpecialOffer?): Price =
        specialOffer?.let {
            if (it.deadline.after(Date())) this * it.discount else this
        } ?: this
}