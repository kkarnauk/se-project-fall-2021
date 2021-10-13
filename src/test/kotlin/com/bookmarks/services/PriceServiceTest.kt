package com.bookmarks.services

import com.bookmarks.models.Book
import com.bookmarks.models.Price
import com.bookmarks.models.Price.Companion.fromCents
import com.bookmarks.models.Price.Companion.toCents
import com.bookmarks.models.Subscription
import com.bookmarks.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@SpringBootTest
internal class PriceServiceTest(@Autowired val priceService: PriceService) {

    @Test
    fun simplePriceCheck() {
        priceService.calculatePurchasePrice(
            getUser(),
            getBook()
        )
    }

    @Test
    fun subscribeDecreasesPrice() {
        getBook().let {
            assertTrue(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = listOf(Subscription.Bookmark)),
                    it
                )!! < it.basePrice
            )
        }
    }

    @Test
    fun `full price if no subscriptions`() {
        getBook().let {
            assertTrue(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = emptyList()),
                    it
                ) == it.basePrice
            )
        }
    }

    @Test
    fun `bought book costs nothing`() {
        assertNull(priceService.calculatePurchasePrice(getUser(bookIds = listOf(getBook().id)), getBook()))
    }

    @Test
    fun `bought another book does not change cost`() {
        getBook().let {
            assertTrue(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = emptyList(), bookIds = listOf(getBook().id + "123")),
                    it
                ) == it.basePrice
            )
        }
    }

    @Test
    fun `discount calculates correctly`() {
        getBook().let {
            assertEquals(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = listOf(Subscription.YandexPlus)),
                    it
                ),
                (it.basePrice.toCents() / 2).fromCents()
            )
        }
    }

    @Test
    fun yandexPlusStressTest() {
        List(1000) {
            getUser(subscriptions = listOf(Subscription.YandexPlus)).let {
                val book = getBook(price = Price(Random.nextInt(), Random.nextInt()))
                assertEquals(
                    priceService.calculatePurchasePrice(it, book)!!,
                    (book.basePrice.toCents() / 2).fromCents()
                )
            }
        }
    }

    companion object {
        private fun getUser(
            id: String = "228",
            name: String = "Petya",
            surname: String = "Surkov",
            nickname: String = "psurkov",
            subscriptions: List<Subscription> = emptyList(),
            bookIds: List<String> = emptyList()
        ) = User(id, name, surname, nickname, subscriptions, bookIds)

        private fun getBook(
            id: String = "2020",
            name: String = "Through Galaxy",
            price: Price = Price(20, 99),
            author: String = "kek22"
        ) = Book(id, name, price, author)
    }
}