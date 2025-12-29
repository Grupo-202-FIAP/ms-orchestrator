package integration.steps

import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.Order
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EPaymentStatus
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus
import com.nextime.orchestrator.domain.services.SagaExecutionService
import com.nextime.orchestrator.utils.JsonConverter
import integration.consumer.ConsumeMessage
import integration.utils.SqsTestSupport
import io.cucumber.java.pt.Dado
import io.cucumber.java.pt.E
import io.cucumber.java.pt.Entao
import io.cucumber.java.pt.Quando
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class SagaIntegrationSteps(
    private val consumeMessage: ConsumeMessage,
    private val sqsTestSupport: SqsTestSupport,
    private val jsonConverter: JsonConverter,
    private val sagaExecutionService: SagaExecutionService,
    private val sqsClient: SqsClient
) {

    private var lastQueueName: String? = null
    private var event: Event? = null

    @Dado("que um evento válido é enviado para a fila {word} com o status {word} e o source {word}")
    fun queUmEventoValidoEhEnviadoParaAFila(queueName: String, status: String, source: String) {
        val status = convertStringToESagaStatus(status) ?: null
        val source = convertStringToESource(source) ?: null

        val normalizedQueueName = queueName.replace("_", "-")
        lastQueueName = normalizedQueueName

        val queueUrl = sqsTestSupport.resolveQueueUrl(sqsClient, normalizedQueueName)

        val order = Order(
            id = UUID.randomUUID(),
            transactinId = UUID.randomUUID(),
            identifier = "ORDER-TEST-001",
            totalPrice = BigDecimal("100.00"),
            totalItems = 1,
            customerId = UUID.randomUUID(),
            paymentStatus = EPaymentStatus.PENDING,
            items = emptyList(),
            createdAt = LocalDateTime.now()
        )

        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = order.id,
            payload = order,
            source = source,
            status = status,
            eventHistory = emptyList(),
            createdAt = LocalDateTime.now()
        )

        sqsClient.sendMessage(
            SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(jsonConverter.toJson(event))
                .build()
        )
    }

    @Quando("o evento é recebido pelo orquestrador")
    fun oEventoERecebidoPeloOrquestrador() {
        oEventoDeÉRecebidoPeloOrquestrador()
    }

    @Quando("o evento de é recebido pelo orquestrador")
    fun oEventoDeÉRecebidoPeloOrquestrador() {
        val queueUrl = sqsTestSupport.resolveQueueUrl(sqsClient, lastQueueName!!)

        val messages = consumeMessage.receiveMessages(
            sqsClient = sqsClient,
            queueUrl = queueUrl
        )

        assert(messages.isNotEmpty()) {
            "Nenhuma mensagem encontrada na fila $lastQueueName. O orquestrador não recebeu a mensagem."
        }

        try {
            event = jsonConverter.toEvent(messages.first())
        } catch (e: Exception) {
            throw IllegalStateException("Falha ao converter a mensagem recebida em Event: ${e.message}", e)
        }

        Thread.sleep(1000)
    }

    @Entao("o orquestrador deve redirecionar o evento para {int} filas de redirecionamento")
    fun oOrquestradorDeveRedirecionarOEventoParaQuantidadeFilasDeRedirecionamento(quantidade: Int) {
        val expectedQueues = sagaExecutionService.getAllNextQueues(event!!)

        println("=== DEBUG - Step @Entao ===")
        println("lastQueueName: $lastQueueName")
        println("event.source: ${event!!.source}")
        println("event.status: ${event!!.status}")
        println("event.id: ${event!!.id}")
        println("event.transactionId: ${event!!.transactionId}")
        println("event.orderId: ${event!!.orderId}")


        assert(expectedQueues.size == quantidade) {
            "Esperado redirecionamento para $quantidade fila(s), mas o SAGA_HANDLER indica ${expectedQueues.size} fila(s): $expectedQueues"
        }
    }

    private fun oEventoDeveSerRedirecionadoParaAsFilas(fila1: EQueues, fila2: EQueues?) {

        val expectedQueues = sagaExecutionService.getAllNextQueues(event!!)

        val expectedQueuesFromTest = if (fila2 != null) {
            listOf(fila1, fila2)
        } else {
            listOf(fila1)
        }

        expectedQueuesFromTest.forEach { queue ->
            assert(expectedQueues.contains(queue)) {
                "A fila $queue não está nas filas esperadas pelo SAGA_HANDLER. Filas esperadas: $expectedQueues"
            }
        }
    }

    @E("o evento deve ser redirecionado para as filas {word} e {word}")
    fun oEventoDeveSerRedirecionadoParaAsFilasFila1EFila2(fila1Name: String, fila2Name: String) {
        val fila1 = convertStringToEQueues(fila1Name)
        val fila2 = if (fila2Name == "null" || fila2Name.isBlank()) null else convertStringToEQueues(fila2Name)
        oEventoDeveSerRedirecionadoParaAsFilas(fila1, fila2)
    }

    @E("o evento deve ser redirecionado para as filas {word}")
    fun oEventoDeveSerRedirecionadoParaAsFilasFila1(fila1Name: String) {
        val fila1 = convertStringToEQueues(fila1Name)
        oEventoDeveSerRedirecionadoParaAsFilas(fila1, null)
    }

    private fun convertStringToEQueues(queueName: String): EQueues {
        val normalizedQueueName = queueName.replace("_", "-")
        return EQueues.entries.find { it.queueName == normalizedQueueName }
            ?: throw IllegalArgumentException("Fila não encontrada: $queueName (normalizado: $normalizedQueueName)")
    }

    private fun convertStringToESagaStatus(status: String): ESagaStatus? {

        if (status.isBlank() || status.equals("null", ignoreCase = true)) {
            return null
        }

        val normalized = status
            .replace("-", "_")
            .replace(" ", "_")

        return ESagaStatus.entries.find { it.status == normalized }
            ?: throw IllegalArgumentException("Status não reconhecido: $status")
    }

    private fun convertStringToESource(sourceString: String): EEventSource? {
        if (sourceString.isBlank() || sourceString.equals("null", ignoreCase = true)) {
            return null
        }


        return EEventSource.entries.find { it.source == sourceString }
            ?: throw IllegalArgumentException("Source não reconhecido: $sourceString")
    }
}
