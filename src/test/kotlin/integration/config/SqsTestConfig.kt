package integration.config

import integration.consumer.ConsumeMessage
import integration.utils.SqsTestSupport
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import java.net.URI

@TestConfiguration
class SqsTestConfig {

    @Bean
    fun sqsTestClient(): SqsClient =
        SqsClient.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test")
                )
            )
            .build()

    @Bean
    fun consumeMessage(): ConsumeMessage =
        ConsumeMessage()

    @Bean
    fun sqsTestSupport(sqsClient: SqsClient): SqsTestSupport =
        SqsTestSupport(sqsClient)
}
