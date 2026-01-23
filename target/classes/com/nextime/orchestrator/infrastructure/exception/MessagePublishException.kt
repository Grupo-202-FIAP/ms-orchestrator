package com.nextime.orchestrator.infrastructure.exception

class MessagePublishException(
    queueUrl: String?,
    cause: Throwable
) : RuntimeException(
    "Falha ao publicar a mensagem na fila: $queueUrl",
    cause
)