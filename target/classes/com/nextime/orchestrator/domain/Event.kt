package com.nextime.orchestrator.domain

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus
import org.intellij.lang.annotations.Identifier
import java.time.LocalDateTime
import java.util.*


data class Event(

    @Identifier
    val id: UUID? = UUID.randomUUID(),

    val transactionId: UUID = UUID.randomUUID(),
    val orderId: UUID = UUID.randomUUID(),

    val payload: Order?,

    var source: EEventSource?,
    var status: ESagaStatus?,

    var eventHistory: List<History> = emptyList(),

    val createdAt: LocalDateTime? = LocalDateTime.now()
) {
    fun addToHistory(history: History): Event =
        copy(eventHistory = eventHistory + history)
}