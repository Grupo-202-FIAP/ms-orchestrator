package com.nextime.orchestrator.application.exception

class SagaProcessingException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)