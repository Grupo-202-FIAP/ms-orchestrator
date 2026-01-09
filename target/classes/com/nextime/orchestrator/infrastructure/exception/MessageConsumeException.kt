package com.nextime.orchestrator.infrastructure.exception

class MessageConsumeException(
    message: String,
    cause: Throwable
) : RuntimeException(message, cause)