package com.nextime.orchestrator.infrastructure.adapters

import org.junit.jupiter.api.Test

class LoggerAdapterTest {

    @Test
    fun `logger adapter methods should not throw`() {
        val adapter = LoggerAdapter()
        adapter.info("info {}", "a")
        adapter.debug("debug {}", "b")
        adapter.warn("warn {}", "c")
        adapter.error("error {}", "d")
        adapter.error("error with throwable", Throwable("t"))
    }
}

