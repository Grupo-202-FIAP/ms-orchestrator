package integration.consumer

import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

class ConsumeMessage {
    fun receiveMessages(
        sqsClient: SqsClient,
        queueUrl: String,
        maxMessages: Int = 10
    ): List<String> {

        val request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(maxMessages)
            .waitTimeSeconds(3) // Aumentado para dar mais tempo para a mensagem estar disponível
            .visibilityTimeout(0)
            .build()

        val response = sqsClient.receiveMessage(request)

        return response.messages().map { it.body() }
    }
}