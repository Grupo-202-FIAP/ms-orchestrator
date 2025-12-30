package com.nextime.orchestrator.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus

class ProductOrderItemHistoryEventTest {

    @Test
    fun `product fields accessible and toString contains name`() {
        val p = Product(UUID.randomUUID(), "prod", BigDecimal("9.99"))
        assertEquals("prod", p.name)
        assertEquals(BigDecimal("9.99"), p.unitPrice)
        assertTrue(p.toString().contains("prod"))
    }

    @Test
    fun `orderItem copy changes quantity`() {
        val p = Product(UUID.randomUUID(), "prod", BigDecimal.ONE)
        val item = OrderItem(UUID.randomUUID(), p, 2)
        val item2 = item.copy(quantity = 5)
        assertEquals(2, item.quantity)
        assertEquals(5, item2.quantity)
    }

    @Test
    fun `history defaults createdAt and fields set`() {
        val h = History(EEventSource.PAYMENT, ESagaStatus.SUCCESS, "ok")
        assertNotNull(h.createdAt)
        assertEquals(ESagaStatus.SUCCESS, h.status)
        assertEquals(EEventSource.PAYMENT, h.source)
    }

    @Test
    fun `event addToHistory appends item`() {
        val e = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PRODUCTION,
            status = ESagaStatus.SUCCESS
        )

        val h = History(EEventSource.PRODUCTION, ESagaStatus.SUCCESS, "done")
        val e2 = e.addToHistory(h)
        assertEquals(0, e.eventHistory.size)
        assertEquals(1, e2.eventHistory.size)
        assertEquals(h, e2.eventHistory[0])
    }
}

