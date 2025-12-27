package com.nextime.orchestrator.application.exception

class InvalidSagaEventException(
    message: String = "O source e o status do evento não podem ser nulos"
) : RuntimeException(message)