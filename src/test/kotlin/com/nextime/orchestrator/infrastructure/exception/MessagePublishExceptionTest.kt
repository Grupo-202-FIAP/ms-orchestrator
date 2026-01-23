package com.nextime.orchestrator.infrastructure.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MessagePublishExceptionTest {

    @Test
    fun `MessagePublishException should have correct message with queueUrl`() {
        val queueUrl = "http://sqs.queue.url"
        val cause = RuntimeException("Publish error")
        val exception = MessagePublishException(queueUrl, cause)

        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("Falha ao publicar a mensagem na fila"))
        assertTrue(exception.message!!.contains(queueUrl))
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `MessagePublishException with null queueUrl should work`() {
        val cause = RuntimeException("Publish error")
        val exception = MessagePublishException(null, cause)

        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("Falha ao publicar a mensagem na fila"))
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `MessagePublishException should be instance of RuntimeException`() {
        val exception = MessagePublishException("queue", RuntimeException())

        assertNotNull(exception as? RuntimeException)
    }
}

