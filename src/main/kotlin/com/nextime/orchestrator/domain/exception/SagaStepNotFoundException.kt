package com.nextime.orchestrator.domain.exception

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus

class SagaStepNotFoundException(
    source: EEventSource,
    status: ESagaStatus
) : RuntimeException(
    "Nenhum passo da saga encontrado para o source=$source e para o status=$status"
)