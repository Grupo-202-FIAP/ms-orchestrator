package integration.config

import com.nextime.orchestrator.OrchestratorApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@CucumberContextConfiguration
@SpringBootTest(
    classes = [
        OrchestratorApplication::class,
        SqsTestConfig::class
    ],
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
class CucumberSpringConfig {
}