package com.nextime.orchestrator.infrastructure.messaging.producer

import com.nextime.orchestrator.application.config.sqs.SqsConfig
import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.infrastructure.exception.MessagePublishException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.stubbing.Answer
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageResponse
import java.util.concurrent.CompletableFuture

class SagaOrchestratorProducerTest {

    class TestLogger : LoggerPort {
        override fun info(msg: String, vararg args: Any?) {}
        override fun debug(msg: String, vararg args: Any?) {}
        override fun warn(msg: String, vararg args: Any?) {}
        override fun error(msg: String, t: Throwable?, vararg args: Any?) {}
        override fun error(msg: String, vararg args: Any?) {}
    }

    class TestSqsConfig(private val client: SqsAsyncClient) : SqsConfig() {
        fun sqsAsyncClient(): SqsAsyncClient = client
    }

    class ThrowingSqsConfig : SqsConfig() {
        fun sqsAsyncClient(): SqsAsyncClient {
            throw RuntimeException("boom")
        }
    }

    @Test
    fun `sendEvent when sqs client creation throws should wrap in MessagePublishException`() {
        val producer = SagaOrchestratorProducer(TestLogger(), ThrowingSqsConfig())

        assertThrows(MessagePublishException::class.java) {
            producer.sendEvent("payload", "queueUrl")
        }
    }
}
