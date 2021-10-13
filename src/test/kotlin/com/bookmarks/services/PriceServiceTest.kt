package com.bookmarks.services

import com.bookmarks.models.*
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
            assertTrue(priceService.calculatePurchasePrice(
                getUser(subscriptions = listOf(Subscription.Bookmark)),
                it
            )!! < it.basePrice)
        }
    }

    @Test
    fun `full price if no subscriptions`() {
        getBook().let {
            assertTrue(priceService.calculatePurchasePrice(
                getUser(subscriptions = emptyList()),
                it
            ) == it.basePrice)
        }
    }

    @Test
    fun `bought book costs nothing`() {
        getUser(bookIds = listOf(getBook().id)).let {
            assertNull(priceService.calculatePurchasePrice(it,getBook()))
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