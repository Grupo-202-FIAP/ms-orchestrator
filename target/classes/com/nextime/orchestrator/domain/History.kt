package com.nextime.orchestrator.domain

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus
import java.time.LocalDateTime

data class History(
    val source: EEventSource,
    val status: ESagaStatus,
    val message: String?,
    val createdAt: LocalDateTime? = LocalDateTime.now()
)
