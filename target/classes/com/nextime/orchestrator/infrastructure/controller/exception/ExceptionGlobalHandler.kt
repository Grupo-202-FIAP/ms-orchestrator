package com.nextime.orchestrator.infrastructure.controller.exception

import com.nextime.orchestrator.application.exception.InvalidSagaEventException
import com.nextime.orchestrator.application.exception.InvalidSagaStateException
import com.nextime.orchestrator.application.exception.SagaProcessingException
import com.nextime.orchestrator.domain.exception.SagaConfigurationException
import com.nextime.orchestrator.domain.exception.SagaStepNotFoundException
import com.nextime.orchestrator.infrastructure.exception.MessageConsumeException
import com.nextime.orchestrator.infrastructure.exception.MessagePublishException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionGlobalHandler {

    companion object {
        private const val SAGA_TITLE = "Saga error"
        private const val SAGA_CONFIG_TITLE = "Saga configuration error"
        private const val MESSAGE_TITLE = "Messaging error"
        private const val INTERNAL_ERROR_TITLE = "Internal server error"
    }

    @ExceptionHandler(InvalidSagaEventException::class)
    fun handleInvalidSagaEvent(ex: InvalidSagaEventException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY)
        problem.title = SAGA_TITLE
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(SagaConfigurationException::class)
    fun handleSagaConfiguration(ex: SagaConfigurationException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problem.title = SAGA_CONFIG_TITLE
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(SagaStepNotFoundException::class)
    fun handleSagaStepNotFound(ex: SagaStepNotFoundException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
        problem.title = SAGA_TITLE
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(InvalidSagaStateException::class)
    fun handleInvalidSagaState(ex: InvalidSagaStateException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problem.title = "Invalid saga state"
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(SagaProcessingException::class)
    fun handleSagaProcessing(ex: SagaProcessingException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problem.title = "Saga processing error"
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(MessagePublishException::class)
    fun handleMessagePublish(ex: MessagePublishException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE)
        problem.title = MESSAGE_TITLE
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(MessageConsumeException::class)
    fun handleMessageConsume(ex: MessageConsumeException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE)
        problem.title = MESSAGE_TITLE
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problem.title = "Invalid argument"
        problem.detail = ex.message
        return problem
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problem.title = INTERNAL_ERROR_TITLE
        problem.detail = "An unexpected error occurred"
        return problem
    }
}
