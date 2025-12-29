package com.nextime.orchestrator.application.ports.`in`

import com.nextime.orchestrator.domain.Event

interface OrchestrationUseCase {
    fun handleSaga(event: Event)
    fun startSaga(event: Event)
}

