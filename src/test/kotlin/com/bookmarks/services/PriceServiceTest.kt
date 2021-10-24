package com.bookmarks.services

import com.bookmarks.models.*
import com.bookmarks.models.Price.Companion.fromCents
import com.bookmarks.models.Price.Companion.toCents
import kotlin.random.Random
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
                    getUser(subscriptions = mutableSetOf(Subscription.Bookmark)),
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
                    getUser(subscriptions = mutableSetOf()),
                    it
                ),
                it.basePrice
            )
        }
    }

    @Test
    fun `bought book costs nothing`() {
        assertNull(priceService.calculatePurchasePrice(getUser(bookIds = mutableSetOf(getBook().id)), getBook()))
    }

    @Test
    fun `bought another book does not change cost`() {
        getBook().let {
            assertTrue(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = mutableSetOf(), bookIds = mutableSetOf(getBook().id + 123u)),
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
                    getUser(subscriptions = mutableSetOf(Subscription.YandexPlus)),
                    it
                ),
                (it.basePrice.toCents() / 2).fromCents()
            )
        }
    }

    @Test
    fun `yandex plus stress test`() {
        List(1000) {
            getUser(subscriptions = mutableSetOf(Subscription.YandexPlus)).let {
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
            emptyList()
        )
    }

    @Test
    fun `simple purchase book bundle of one book if no subscriptions`() {
        getBook().let {
            assertTrue(
                priceService.calculatePurchasePrice(
                    getUser(subscriptions = mutableSetOf()),
                    listOf(it)
                ) == it.basePrice
            )
        }
    }

    @Test
    fun `5 books gives 10 percent discount`() {
        assertEquals(
            priceService.calculatePurchasePrice(
                getUser(subscriptions = mutableSetOf()),
                List(5) { getBook() }
            ),
            getBook().basePrice * 5.0 * 0.9
        )
    }

    @Test
    fun `10 books gives 15 percent discount`() {
        assertEquals(
            priceService.calculatePurchasePrice(
                getUser(subscriptions = mutableSetOf()),
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
                emptyList()
            )
        )
    }

    @Test
    fun `included book costs nothing`() {
        assertNull(
            priceService.calculatePurchasePrice(
                getUser(subscriptions = mutableSetOf(Subscription.Bookmark)),
                getBook(id = 1u)
            )
        )
    }

    @Test
    fun `user can buy book only once`() {
        val user = getUser()
        val book = getBook()
        user.purchase(book)
        assertNull(priceService.calculatePurchasePrice(user, book))
    }

    @Test
    fun `subscription for author`() {
        val author = getAuthor()
        val user = getUser(subscriptions = mutableSetOf(Subscription.ForAuthor(author)))
        val book = getBook(authorId = author.id)
        assertNull(priceService.calculatePurchasePrice(user, book))
    }

    companion object {
        private fun getUser(
            id: UInt = 228u,
            name: String = "Petya",
            surname: String = "Surkov",
            nickname: String = "psurkov",
            subscriptions: MutableSet<Subscription> = mutableSetOf(),
            bookIds: MutableSet<UInt> = mutableSetOf()
        ) = User(id, name, surname, nickname, subscriptions, bookIds)

        private fun getBook(
            id: UInt = 2020u,
            name: String = "Through Galaxy",
            price: Price = Price(20, 99),
            authorId: UInt = getAuthor().id
        ) = Book(id, name, price, authorId)

        private fun getAuthor(
            id: UInt = 2u,
            name: String = "Boris",
            surname: String = "Novikov"
        ) = Author(id, name, surname)
    }
}