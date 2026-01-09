package com.nextime.orchestrator.domain

import java.util.UUID

data class OrderItem(
    val id: UUID,
    val product: Product,
    val quantity: Int
)
