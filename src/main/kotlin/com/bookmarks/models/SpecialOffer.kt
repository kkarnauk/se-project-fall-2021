package com.bookmarks.models

import java.util.*

data class SpecialOffer(val deadline: Date, val discount: Double, val freeRentWeekBookIds: List<UInt> = listOf())
