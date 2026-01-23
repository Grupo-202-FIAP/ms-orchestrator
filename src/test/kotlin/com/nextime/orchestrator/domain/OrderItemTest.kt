package com.nextime.orchestrator.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class OrderItemTest {

    @Test
    fun `orderItem should have all fields accessible`() {
        val itemId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val product = Product(
            id = productId,
            name = "Product Test",
            unitPrice = BigDecimal("29.99")
        )

        val orderItem = OrderItem(
            id = itemId,
            product = product,
            quantity = 5
        )

        assertEquals(itemId, orderItem.id)
        assertEquals(product, orderItem.product)
        assertEquals(5, orderItem.quantity)
        assertEquals("Product Test", orderItem.product.name)
        assertEquals(BigDecimal("29.99"), orderItem.product.unitPrice)
    }

    @Test
    fun `orderItem copy should create new instance with modified fields`() {
        val product = Product(
            id = UUID.randomUUID(),
            name = "Product",
            unitPrice = BigDecimal("10.00")
        )

        val original = OrderItem(
            id = UUID.randomUUID(),
            product = product,
            quantity = 2
        )

        val copied = original.copy(quantity = 10)

        assertEquals(2, original.quantity)
        assertEquals(10, copied.quantity)
        assertEquals(original.id, copied.id)
        assertEquals(original.product, copied.product)
    }
}

