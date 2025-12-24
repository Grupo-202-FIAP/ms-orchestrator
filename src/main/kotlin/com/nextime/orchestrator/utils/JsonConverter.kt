package com.nextime.orchestrator.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.domain.Event
import org.springframework.stereotype.Component

@Component
class JsonConverter(
    private val objectMapper: ObjectMapper,
    private val logger: LoggerPort
) {

    fun toJson(value: Any): String =
        try {
            objectMapper.writeValueAsString(value)
        } catch (ex: Exception) {
            logger.error(
                "[toJson] Falha para converter objeto para JSON",
                ex
            )
            throw RuntimeException(
                "[toJson] Falha para converter objeto para JSON",
                ex
            )
        }

    fun toEvent(json: String): Event =
        try {
            objectMapper.readValue(json, Event::class.java)
        } catch (ex: Exception) {
            logger.error(
                "[toEvent] Falha para converter JSON para Event",
                ex
            )
            throw RuntimeException(
                "[toEvent] Falha para converter JSON para Event",
                ex
            )
        }


}