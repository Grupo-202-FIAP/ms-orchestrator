package com.nextime.orchestrator.domain

import java.math.BigDecimal
import java.util.UUID

data class Product(
    val id: UUID,
    val name: String,
    val unitPrice: BigDecimal,
)
