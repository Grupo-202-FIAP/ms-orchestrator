package com.nextime.orchestrator.infrastructure.adapters

import com.nextime.orchestrator.application.gateways.LoggerPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LoggerAdapter : LoggerPort {

    private val logger: Logger =
        LoggerFactory.getLogger(LoggerAdapter::class.java)
    public override fun info(msg: String?, vararg args: Any?) {
        logger.info(msg, *args)
    }

    public override fun debug(msg: String?, vararg args: Any?) {
        logger.debug(msg, *args)
    }

    public override fun warn(msg: String?, vararg args: Any?) {
        logger.warn(msg, *args)
    }

    public override fun error(msg: String, t: Throwable?, vararg args: Any?) {
        logger.error(String.format(msg, *args), t)
    }

    public override fun error(msg: String?, vararg args: Any?) {
        logger.error(msg, *args)
    }
}