package com.bookmarks.services

import com.bookmarks.models.*
import java.util.*
import org.springframework.stereotype.Component

@Component
object PriceService {
    fun calculateCartPrice(user: User, cart: Cart, specialOffer: SpecialOffer? = null): Price? {
        if (cart.bookList().groupBy { it }.filterValues { it.size > 1 }.isNotEmpty()) return null
        if (cart.bookList().any { it.id in user.purchasedBookIds }) return null

        val purchases = cart.elements.filterIsInstance<Purchase>().map { it.book }
        val rents = cart.elements.filterIsInstance<Rent>()

        val rentPrice = rents.fold(Price(0, 0)) { acc, rent ->
            val rentWeekPrice = calculateWeekRentPrice(user, rent.book, specialOffer) ?: return null
            acc + (rentWeekPrice.toCents() * rent.weeks).fromCents()
        }

        val purchasePrice =
            if (purchases.isEmpty()) 0.fromCents()
            else calculatePurchasePrice(user, purchases, specialOffer) ?: return null

        return rentPrice + purchasePrice
    }

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
        .takeIf { it.isNotEmpty() }
        ?.map { calculatePurchasePrice(user, it) }
        ?.takeIfNoNulls()
        ?.sumOf { it.toCents() }
        ?.fromCents()?.let {
            it * when {
                books.size >= 10 -> 0.8
                books.size >= 5 -> 0.9
                else -> 1.0
            }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T> List<T?>.takeIfNoNulls(): List<T>? {
        return if (null in this) null else this as List<T>
    }

    private fun Price.applySpecialOffer(specialOffer: SpecialOffer?): Price =
        specialOffer?.let {
            if (it.deadline.after(Date())) this * it.discount else this
        } ?: this

    private fun Cart.bookList(): List<Book> = this.elements.map { it.book }
}
