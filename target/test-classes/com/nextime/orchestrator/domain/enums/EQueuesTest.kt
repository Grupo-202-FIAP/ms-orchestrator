package com.nextime.orchestrator.domain.enums

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EQueuesTest {

    @Test
    fun `all queue names should match expected values`() {
        assertEquals("order-queue", EQueues.ORDER_QUEUE.queueName)
        assertEquals("order-callback-queue", EQueues.ORDER_CALLBACK_QUEUE.queueName)
        assertEquals("payment-queue", EQueues.PAYMENT_QUEUE.queueName)
        assertEquals("payment-callback-queue", EQueues.PAYMENT_CALLBACK_QUEUE.queueName)
        assertEquals("production-queue", EQueues.PRODUCTION_QUEUE.queueName)
        assertEquals("production-callback-queue", EQueues.PRODUCTION_CALLBACK_QUEUE.queueName)
    }

    @Test
    fun `all enum values should be accessible`() {
        val allQueues = EQueues.values()

        assertEquals(6, allQueues.size)
        assertTrue(allQueues.contains(EQueues.ORDER_QUEUE))
        assertTrue(allQueues.contains(EQueues.ORDER_CALLBACK_QUEUE))
        assertTrue(allQueues.contains(EQueues.PAYMENT_QUEUE))
        assertTrue(allQueues.contains(EQueues.PAYMENT_CALLBACK_QUEUE))
        assertTrue(allQueues.contains(EQueues.PRODUCTION_QUEUE))
        assertTrue(allQueues.contains(EQueues.PRODUCTION_CALLBACK_QUEUE))
    }
}

