package com.bookmarks.models

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val nickname: String,
    val subscribes: List<Subscribe>,
    val purchasedBookIds: List<String>,
)