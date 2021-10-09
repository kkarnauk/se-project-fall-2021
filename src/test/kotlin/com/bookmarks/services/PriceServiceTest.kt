package com.bookmarks.services

import com.bookmarks.models.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class PriceServiceTest(@Autowired val priceService: PriceService) {

    @Test
    fun simplePriceCheck() {
        priceService.calculatePrice(
            getUser(),
            getBook()
        )
    }

    @Test
    fun subscribeDecreasesPrice() {
        getBook().let {
            assertTrue(priceService.calculatePrice(
                getUser(subscribes = listOf(Subscribe.Bookmark)),
                it
            ) < it.basePrice)
        }
    }

    companion object {
        private fun getUser(
            id: String = "228",
            name: String = "Petya",
            surname: String = "Surkov",
            nickname: String = "psurkov",
            subscribes: List<Subscribe> = emptyList(),
            bookIds: List<String> = emptyList()
        ) = User(id, name, surname, nickname, subscribes, bookIds)

        private fun getBook(
            id: String = "2020",
            name: String = "Through Galaxy",
            price: Price = Price(20, 99),
            author: String = "kek22"
        ) = Book(id, name, price, author)
    }
}