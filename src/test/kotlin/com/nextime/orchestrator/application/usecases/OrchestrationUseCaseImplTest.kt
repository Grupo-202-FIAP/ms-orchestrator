package com.nextime.orchestrator.application.usecases

import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.application.ports.out.MessageProducerPort
import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus
import com.nextime.orchestrator.domain.services.SagaExecutionService
import com.nextime.orchestrator.utils.JsonConverter
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*

class OrchestrationUseCaseImplTest {

    class TestLogger : LoggerPort {
        override fun info(msg: String, vararg args: Any?) {}
        override fun debug(msg: String, vararg args: Any?) {}
        override fun warn(msg: String, vararg args: Any?) {}
        override fun error(msg: String, t: Throwable, vararg args: Any?) {}
        override fun error(msg: String, vararg args: Any?) {}
    }

    class TestMessageProducer : MessageProducerPort {
        var sends: MutableList<Pair<String, String>> = mutableListOf()
        override fun sendEvent(message: String, queueName: String) {
            sends.add(message to queueName)
        }
    }

    class TestSagaExecutionService(var queuesToReturn: List<EQueues> = listOf()) : SagaExecutionService() {
        override fun getAllNextQueues(event: Event): List<EQueues> = queuesToReturn
    }

    private val logger = TestLogger()
    private val jsonConverter = JsonConverter(logger)
    private val sagaExecutionService = TestSagaExecutionService()
    private val messageProducer = TestMessageProducer()

    private val usecase = OrchestrationUseCaseImpl(
        logger,
        jsonConverter,
        sagaExecutionService,
        messageProducer
    )

    @Test
    fun `handleSaga start should send to all queues returned by sagaExecutionService`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = null,
            status = null
        )

        (sagaExecutionService as TestSagaExecutionService).queuesToReturn = listOf(EQueues.PRODUCTION_QUEUE, EQueues.ORDER_CALLBACK_QUEUE)

        usecase.handleSaga(event)

        // expect that two sends were made
        assert((messageProducer as TestMessageProducer).sends.size == 2)
    }

    @Test
    fun `startSaga with no queues should throw SagaConfigurationException`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = null,
            status = null
        )

        (sagaExecutionService as TestSagaExecutionService).queuesToReturn = emptyList()

        assertThrows(com.nextime.orchestrator.domain.exception.SagaConfigurationException::class.java) {
            usecase.startSaga(event)
        }
    }

    @Test
    fun `handleSaga when producer throws should wrap in SagaProcessingException`() {
        // to simulate producer throwing, we create a producer that throws
        val throwingProducer = object : MessageProducerPort {
            override fun sendEvent(message: String, queueName: String) {
                throw RuntimeException("fail")
            }
        }

        val usecaseWithThrowingProducer = OrchestrationUseCaseImpl(
            logger,
            jsonConverter,
            sagaExecutionService,
            throwingProducer
        )

        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = null,
            status = null
        )

        (sagaExecutionService as TestSagaExecutionService).queuesToReturn = listOf(EQueues.PRODUCTION_QUEUE)

        assertThrows(com.nextime.orchestrator.application.exception.SagaProcessingException::class.java) {
            usecaseWithThrowingProducer.handleSaga(event)
        }
    }

    @Test
    fun `continueSaga should send to next queue`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PAYMENT,
            status = ESagaStatus.SUCCESS
        )

        (sagaExecutionService as TestSagaExecutionService).queuesToReturn = listOf(EQueues.PRODUCTION_QUEUE)

        usecase.continueSaga(event)

        assert((messageProducer as TestMessageProducer).sends.size == 1)
    }

    @Test
    fun `finishSagaSuccess should notify finished queue`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PRODUCTION,
            status = ESagaStatus.SUCCESS
        )

        // ensure sagaExecutionService.getNextQueue will not throw
        (sagaExecutionService as TestSagaExecutionService).queuesToReturn = listOf(EQueues.ORDER_CALLBACK_QUEUE)

        // when finish, it should call notifyFinishedSaga which uses ORDER_CALLBACK_QUEUE
        usecase.finishSagaSuccess(event)

        assert((messageProducer as TestMessageProducer).sends.size == 1)
        assert((messageProducer as TestMessageProducer).sends.first().second == EQueues.ORDER_CALLBACK_QUEUE.queueName)
    }

    @Test
    fun `finishSagaFail should notify finished queue`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PRODUCTION,
            status = ESagaStatus.FAIL
        )

        (sagaExecutionService as TestSagaExecutionService).queuesToReturn = listOf(EQueues.ORDER_CALLBACK_QUEUE)

        usecase.finishSagaFail(event)

        assert((messageProducer as TestMessageProducer).sends.size == 1)
        assert((messageProducer as TestMessageProducer).sends.first().second == EQueues.ORDER_CALLBACK_QUEUE.queueName)
    }

    @Test
    fun `handleRollback should send to rollback queues`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.PAYMENT,
            status = ESagaStatus.ROLLBACK_PENDING
        )

        (sagaExecutionService as TestSagaExecutionService).queuesToReturn = listOf(EQueues.PAYMENT_QUEUE, EQueues.PRODUCTION_QUEUE)

        usecase.handleRollback(event)

        assert((messageProducer as TestMessageProducer).sends.size == 2)
    }
}
