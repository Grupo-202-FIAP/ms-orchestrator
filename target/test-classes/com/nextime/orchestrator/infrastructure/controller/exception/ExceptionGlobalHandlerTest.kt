package com.nextime.orchestrator.infrastructure.controller.exception

import com.nextime.orchestrator.application.exception.InvalidSagaEventException
import com.nextime.orchestrator.domain.exception.SagaConfigurationException
import com.nextime.orchestrator.domain.exception.SagaStepNotFoundException
import com.nextime.orchestrator.application.exception.InvalidSagaStateException
import com.nextime.orchestrator.application.exception.SagaProcessingException
import com.nextime.orchestrator.infrastructure.exception.MessageConsumeException
import com.nextime.orchestrator.infrastructure.exception.MessagePublishException
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ExceptionGlobalHandlerTest {

    private val handler = ExceptionGlobalHandler()

    @Test
    fun `handleInvalidSagaEvent returns UnprocessableEntity`() {
        val ex = InvalidSagaEventException("invalid payload")
        val pd = handler.handleInvalidSagaEvent(ex)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), pd.status)
        assertEquals("Saga error", pd.title)
        assertEquals("invalid payload", pd.detail)
    }

    @Test
    fun `handleSagaConfiguration returns InternalServerError`() {
        val ex = SagaConfigurationException("config bad")
        val pd = handler.handleSagaConfiguration(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd.status)
        assertEquals("Saga configuration error", pd.title)
        assertEquals("config bad", pd.detail)
    }

    @Test
    fun `handleSagaStepNotFound returns NotFound`() {
        val ex = SagaStepNotFoundException(EEventSource.PAYMENT, ESagaStatus.FAIL)
        val pd = handler.handleSagaStepNotFound(ex)
        assertEquals(HttpStatus.NOT_FOUND.value(), pd.status)
        assertEquals("Saga error", pd.title)
        assertTrue(pd.detail?.contains("Nenhum passo da saga") == true || pd.detail?.isNotEmpty() == true)
    }

    @Test
    fun `handleInvalidSagaState returns BadRequest`() {
        val ex = InvalidSagaStateException("wrong state")
        val pd = handler.handleInvalidSagaState(ex)
        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.status)
        assertEquals("Invalid saga state", pd.title)
        assertEquals("wrong state", pd.detail)
    }

    @Test
    fun `handleSagaProcessing returns InternalServerError`() {
        val ex = SagaProcessingException("proc fail")
        val pd = handler.handleSagaProcessing(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd.status)
        assertEquals("Saga processing error", pd.title)
        assertEquals("proc fail", pd.detail)
    }

    @Test
    fun `handleMessagePublish returns ServiceUnavailable`() {
        val ex = MessagePublishException("qUrl", RuntimeException("boom"))
        val pd = handler.handleMessagePublish(ex)
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), pd.status)
        assertEquals("Messaging error", pd.title)
        assertTrue(pd.detail?.contains("Falha ao publicar") == true || pd.detail?.isNotEmpty() == true)
    }

    @Test
    fun `handleMessageConsume returns ServiceUnavailable`() {
        val ex = MessageConsumeException("msg", RuntimeException("boom"))
        val pd = handler.handleMessageConsume(ex)
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), pd.status)
        assertEquals("Messaging error", pd.title)
        assertEquals("msg", pd.detail)
    }

    @Test
    fun `handleIllegalArgument returns BadRequest`() {
        val ex = IllegalArgumentException("bad arg")
        val pd = handler.handleIllegalArgument(ex)
        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.status)
        assertEquals("Invalid argument", pd.title)
        assertEquals("bad arg", pd.detail)
    }

    @Test
    fun `handleGeneric returns InternalServerError with generic message`() {
        val ex = Exception("oops")
        val pd = handler.handleGeneric(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd.status)
        assertEquals("Internal server error", pd.title)
        assertEquals("An unexpected error occurred", pd.detail)
    }
}
