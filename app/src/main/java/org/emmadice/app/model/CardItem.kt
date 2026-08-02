package org.emmadice.app.model

data class CardItem(
    val id: Int,
    val title: String,
    val image: Int,
    val audio: String? = null
)