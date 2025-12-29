package com.nextime.orchestrator.domain.enums

enum class EQueues(val queueName: String) {
    ORDER_QUEUE("order-queue"),
    ORDER_CALLBACK_QUEUE("order-callback-queue"),
    PAYMENT_QUEUE("payment-queue"),
    PAYMENT_CALLBACK_QUEUE("payment-callback-queue"),
    PRODUCTION_QUEUE("production-queue"),
    PRODUCTION_CALLBACK_QUEUE("production-callback-queue");
}
