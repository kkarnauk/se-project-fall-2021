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
    fun `simple price check`() {
        priceService.calculatePurchasePrice(
            getUser(),
            getBook()
        )
    }

    @Test
    fun `subscribe decrease price`() {
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
            assertEquals(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = emptyList()),
                    it
                ),
                it.basePrice
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
                    getUser(subscriptions = emptyList(), bookIds = listOf(getBook().id + 123u)),
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
    fun `yandex plus stress test`() {
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

    @Test
    fun `simple purchase book bundle`() {
        priceService.calculatePurchasePrice(
            getUser(),
            listOf()
        )
    }

    @Test
    fun `simple purchase book bundle of one book if no subscriptions`() {
        getBook().let {
            assertTrue(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = emptyList()),
                    listOf(it)
                ) == it.basePrice
            )
        }
    }

    @Test
    fun `5 books gives 10 percent discount`() {
        assertEquals(
            priceService.calculatePurchasePrice(
                getUser(subscriptions = emptyList()),
                List(5) { getBook() }
            ),
            getBook().basePrice * 5.0 * 0.9
        )
    }

    @Test
    fun `10 books gives 15 percent discount`() {
        assertEquals(
            priceService.calculatePurchasePrice(
                getUser(subscriptions = emptyList()),
                List(10) { getBook() }
            ),
            getBook().basePrice * 10.0 * 0.8
        )
    }

    @Test
    fun `empty list means no price`() {
        assertNull(
            priceService.calculatePurchasePrice(
                getUser(),
                listOf()
            )
        )
    }

    @Test
    fun `included book costs nothing`() {
        assertTrue(
            priceService.calculatePurchasePrice(
                getUser(subscriptions = listOf(Subscription.Bookmark)),
                getBook(id = 1u)
            ) == Price(0, 0)
        )
    }

    @Test
    fun `user buys book and than he cannot buy it again`() {
        val user = getUser()
        val book = getBook()
        user.buy(book)
        assertNull(priceService.calculatePurchasePrice(user, book))
    }

    companion object {
        private fun getUser(
            id: UInt = 228u,
            name: String = "Petya",
            surname: String = "Surkov",
            nickname: String = "psurkov",
            subscriptions: List<Subscription> = emptyList(),
            bookIds: List<UInt> = emptyList()
        ) = User(id, name, surname, nickname, subscriptions, bookIds)

        private fun getBook(
            id: UInt = 2020u,
            name: String = "Through Galaxy",
            price: Price = Price(20, 99),
            author: String = "kek22"
        ) = Book(id, name, price, author)
    }
}