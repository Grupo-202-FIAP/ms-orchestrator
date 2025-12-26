package com.nextime.orchestrator.domain

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus

object SagaHandler {

    const val EVENT_SOURCE_INDEX = 0
    const val STATUS_INDEX = 1
    const val QUEUE_INDEX = 2

    val SAGA_HANDLER: List<Triple<EEventSource, ESagaStatus, EQueues>> = listOf(

        // =========================
        // 1️⃣ START DA SAGA (fan-out)
        // =========================
        Triple(
            EEventSource.ORCHESTRATOR,
            ESagaStatus.SUCCESS,
            EQueues.PRODUCTION_QUEUE   // RECEIVED
        ),
        Triple(
            EEventSource.ORCHESTRATOR,
            ESagaStatus.SUCCESS,
            EQueues.PAYMENT_QUEUE      // PROCESS
        ),

        // =========================
        // 2️⃣ PAYMENT
        // =========================

        // Payment SUCCESS → Production PREPARING
        Triple(
            EEventSource.PAYMENT,
            ESagaStatus.SUCCESS,
            EQueues.PRODUCTION_QUEUE
        ),

        // Payment FAIL → inicia rollback da Production
        Triple(
            EEventSource.PAYMENT,
            ESagaStatus.FAIL,
            EQueues.PRODUCTION_CALLBACK_QUEUE
        ),

        // Payment ROLLBACK_PENDING → avisa Order
        Triple(
            EEventSource.PAYMENT,
            ESagaStatus.ROLLBACK_PENDING,
            EQueues.ORDER_CALLBACK_QUEUE
        ),

        // =========================
        // 3️⃣ PRODUCTION
        // =========================

        // Production SUCCESS → finaliza saga
        Triple(
            EEventSource.PRODUCTION,
            ESagaStatus.SUCCESS,
            EQueues.ORDER_CALLBACK_QUEUE
        ),

        // Production FAIL → inicia rollback do Payment
        Triple(
            EEventSource.PRODUCTION,
            ESagaStatus.FAIL,
            EQueues.PAYMENT_CALLBACK_QUEUE
        ),

        // Production ROLLBACK_PENDING → avisa Order
        Triple(
            EEventSource.PRODUCTION,
            ESagaStatus.ROLLBACK_PENDING,
            EQueues.ORDER_CALLBACK_QUEUE
        )
    )
}
