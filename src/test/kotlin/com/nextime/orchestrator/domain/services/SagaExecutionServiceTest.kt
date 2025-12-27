package com.nextime.orchestrator.domain.services

import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus
import com.nextime.orchestrator.domain.exception.SagaStepNotFoundException
import com.nextime.orchestrator.application.exception.InvalidSagaEventException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class SagaExecutionServiceTest {

    private val service = SagaExecutionService()

    @Test
    fun `getNextQueue should return first queue for valid event`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PRODUCTION,
            status = ESagaStatus.SUCCESS
        )

        val queue = service.getNextQueue(event)

        assertNotNull(queue)
        assertEquals(EQueues.ORDER_CALLBACK_QUEUE, queue)
    }

    @Test
    fun `getNextQueue unknown combination should throw SagaStepNotFoundException`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.ORCHESTRATOR,
            status = ESagaStatus.FAIL
        )

        assertThrows(SagaStepNotFoundException::class.java) {
            service.getNextQueue(event)
        }
    }

    @Test
    fun `getAllNextQueues with null source should throw InvalidSagaEventException`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = null,
            status = null
        )

        assertThrows(InvalidSagaEventException::class.java) {
            service.getAllNextQueues(event)
        }
    }

    @Test
    fun `isSagaFinished returns true when next queues contain order callback`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PRODUCTION,
            status = ESagaStatus.SUCCESS
        )

        assertTrue(service.isSagaFinished(event))
    }

    @Test
    fun `getAllNextQueues for payment rollback pending returns multiple queues`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PAYMENT,
            status = ESagaStatus.ROLLBACK_PENDING
        )

        val queues = service.getAllNextQueues(event)
        assertNotNull(queues)
        assertTrue(queues.size >= 2)
        assertTrue(queues.contains(EQueues.PAYMENT_QUEUE))
        assertTrue(queues.contains(EQueues.PRODUCTION_QUEUE))
    }
}
