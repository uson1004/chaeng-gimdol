package com.yuseob.chaenggimdol.domain.item

class SeedDefaultItemsUseCase(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(names: Set<String>) {
        names.forEach { name ->
            repository.upsert(
                UserItem(
                    name = name,
                    category = categoryFor(name),
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
