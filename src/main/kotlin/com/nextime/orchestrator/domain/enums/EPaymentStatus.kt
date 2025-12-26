package com.nextime.orchestrator.domain.enums

enum class EPaymentStatus(val status: String) {
    PROCESSED("PROCESSED"),
    PENDING("PENDING"),
    EXPIRED("EXPIRED");
}