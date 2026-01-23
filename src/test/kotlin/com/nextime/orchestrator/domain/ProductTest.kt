package com.nextime.orchestrator.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ProductTest {

    @Test
    fun `product should have all fields accessible`() {
        val productId = UUID.randomUUID()
        val product = Product(
            id = productId,
            name = "Test Product",
            unitPrice = BigDecimal("99.99")
        )

        assertEquals(productId, product.id)
        assertEquals("Test Product", product.name)
        assertEquals(BigDecimal("99.99"), product.unitPrice)
    }

    @Test
    fun `product copy should create new instance with modified fields`() {
        val original = Product(
            id = UUID.randomUUID(),
            name = "Original Product",
            unitPrice = BigDecimal("50.00")
        )

        val copied = original.copy(
            name = "Updated Product",
            unitPrice = BigDecimal("75.00")
        )

        assertEquals("Original Product", original.name)
        assertEquals(BigDecimal("50.00"), original.unitPrice)
        assertEquals("Updated Product", copied.name)
        assertEquals(BigDecimal("75.00"), copied.unitPrice)
        assertEquals(original.id, copied.id)
    }

    @Test
    fun `product with zero price should work`() {
        val product = Product(
            id = UUID.randomUUID(),
            name = "Free Product",
            unitPrice = BigDecimal.ZERO
        )

        assertEquals(BigDecimal.ZERO, product.unitPrice)
    }
}

