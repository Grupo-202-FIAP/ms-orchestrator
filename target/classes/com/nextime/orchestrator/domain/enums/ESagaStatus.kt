package com.nextime.orchestrator.domain.enums

enum class ESagaStatus(val status: String) {
    SUCCESS("SUCCESS"),
    ROLLBACK_PENDING("ROLLBACK_PENDING"),
    FAIL("FAIL")
}