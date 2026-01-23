package integration.utils

import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest

@Component
class SqsTestSupport(
    private val sqsClient: SqsClient,
) {

    fun resolveQueueUrl(
        sqsClient: SqsClient,
        queueName: String
    ): String =
        sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build()
        ).queueUrl()
}