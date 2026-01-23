package com.nextime.orchestrator.domain

import com.nextime.orchestrator.domain.enums.EPaymentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class OrderTest {

    @Test
    fun `order should have all fields accessible`() {
        val orderId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val order = Order(
            id = orderId,
            transactinId = transactionId,
            identifier = "ORDER-001",
            totalPrice = BigDecimal("100.50"),
            totalItems = 3,
            customerId = customerId,
            paymentStatus = EPaymentStatus.PENDING,
            items = emptyList(),
            createdAt = LocalDateTime.now()
        )

        assertEquals(orderId, order.id)
        assertEquals(transactionId, order.transactinId)
        assertEquals("ORDER-001", order.identifier)
        assertEquals(BigDecimal("100.50"), order.totalPrice)
        assertEquals(3, order.totalItems)
        assertEquals(customerId, order.customerId)
        assertEquals(EPaymentStatus.PENDING, order.paymentStatus)
        assertNotNull(order.createdAt)
    }

    @Test
    fun `order with null optional fields should work`() {
        val order = Order(
            id = UUID.randomUUID(),
            transactinId = UUID.randomUUID(),
            identifier = "ORDER-002",
            paymentStatus = EPaymentStatus.PROCESSED
        )

        assertNull(order.totalPrice)
        assertNull(order.totalItems)
        assertNull(order.customerId)
        assertNull(order.items)
        assertNotNull(order.createdAt)
    }

    @Test
    fun `order copy should create new instance with modified fields`() {
        val original = Order(
            id = UUID.randomUUID(),
            transactinId = UUID.randomUUID(),
            identifier = "ORDER-003",
            paymentStatus = EPaymentStatus.PENDING
        )

        val copied = original.copy(
            identifier = "ORDER-004",
            paymentStatus = EPaymentStatus.PROCESSED
        )

        assertEquals("ORDER-003", original.identifier)
        assertEquals(EPaymentStatus.PENDING, original.paymentStatus)
        assertEquals("ORDER-004", copied.identifier)
        assertEquals(EPaymentStatus.PROCESSED, copied.paymentStatus)
        assertEquals(original.id, copied.id)
    }
}

