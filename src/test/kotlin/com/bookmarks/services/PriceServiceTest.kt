package com.bookmarks.services

import com.bookmarks.models.Book
import com.bookmarks.models.Price
import com.bookmarks.models.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class PriceServiceTest(@Autowired val priceService: PriceService) {

    @Test
    fun calculatePrice() {
        priceService.calculatePrice(
            User("", "", "", "", emptyList(), emptyList()),
            Book("", "", Price(0, 0), "")
        )
    }
}