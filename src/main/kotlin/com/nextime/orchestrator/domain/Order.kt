package com.nextime.orchestrator.domain

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Order(
    val id: UUID,
    val transactinId: UUID,
    val identifier: String,
    val totalPrice: BigDecimal? = null,
    val totalItems: Int? = null,
    val customerId: UUID? = null,
    val items: List<OrderItem>? = null,
    val createdAt: LocalDateTime? = LocalDateTime.now()
)