package com.yuseob.chaenggimdol.domain.item

data class UserItem(
    val id: Long = 0,
    val name: String,
    val category: String,
    val important: Boolean = true,
    val active: Boolean = true,
)
