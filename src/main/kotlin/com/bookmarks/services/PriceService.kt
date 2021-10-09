package com.bookmarks.services

import com.bookmarks.models.Book
import com.bookmarks.models.Price
import com.bookmarks.models.User
import org.springframework.stereotype.Component

@Component
object PriceService {
    fun calculatePrice(user: User, book: Book): Price {
        TODO()
    }
}