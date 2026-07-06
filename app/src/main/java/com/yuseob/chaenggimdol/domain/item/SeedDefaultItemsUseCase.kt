package com.yuseob.chaenggimdol.domain.item

class SeedDefaultItemsUseCase(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(items: List<SeedItem>) {
        items.forEach { item ->
            repository.upsert(
                UserItem(
                    name = item.name,
                    category = categoryFor(item.name),
                    important = item.important,
                    checkHint = item.checkHint?.takeIf(String::isNotBlank)?.trim(),
                ),
            )
        }
    }

    private fun categoryFor(name: String): String = when (name) {
        "휴대폰" -> "phone"
        "이어폰" -> "earphones"
        "충전기" -> "charger"
        "지갑" -> "wallet"
        "우산" -> "umbrella"
        "노트북" -> "laptop"
        else -> "other"
    }
}

data class SeedItem(
    val name: String,
    val important: Boolean = true,
    val checkHint: String? = null,
)
