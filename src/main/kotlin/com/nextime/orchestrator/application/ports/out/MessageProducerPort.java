package com.nextime.orchestrator.application.ports.out;

public interface MessageProducerPort {
    void sendEvent(String payload, String queueUrl);
}

