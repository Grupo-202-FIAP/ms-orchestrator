package com.nextime.orchestrator.domain

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus

object SagaHandler {

    val SAGA_HANDLER: List<Triple<EEventSource, ESagaStatus, EQueues>> = listOf(

        // =========================
        // 1️⃣ START DA SAGA (fan-out)
        // =========================
        Triple(
            EEventSource.ORCHESTRATOR,
            ESagaStatus.SUCCESS,
            EQueues.PRODUCTION_QUEUE
        ),
        Triple(
            EEventSource.ORCHESTRATOR,
            ESagaStatus.SUCCESS,
            EQueues.PAYMENT_QUEUE
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
            EQueues.PRODUCTION_QUEUE
        ),

        // Payment ROLLBACK_PENDING → avisa Production
        Triple(
            EEventSource.PAYMENT,
            ESagaStatus.ROLLBACK_PENDING,
            EQueues.PAYMENT_QUEUE
        ),
        Triple(
            EEventSource.PAYMENT,
            ESagaStatus.ROLLBACK_PENDING,
            EQueues.PRODUCTION_QUEUE
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

        // Production FAIL → Finaliza saga com erro
        Triple(
            EEventSource.PRODUCTION,
            ESagaStatus.FAIL,
            EQueues.ORDER_CALLBACK_QUEUE
        ),

        // Production ROLLBACK_PENDING → avisa Order
        Triple(
            EEventSource.PRODUCTION,
            ESagaStatus.ROLLBACK_PENDING,
            EQueues.PRODUCTION_QUEUE
        ),
        Triple(
            EEventSource.PRODUCTION,
            ESagaStatus.ROLLBACK_PENDING,
            EQueues.PAYMENT_QUEUE
        )
    )
}
