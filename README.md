# MS Orchestrator

Microsserviço orquestrador responsável por gerenciar o fluxo de Saga Pattern para processamento de pedidos, coordenando a comunicação entre os serviços de **Payment**, **Production** e **Order**.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Fluxo da Saga](#fluxo-da-saga)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Configuração](#configuração)
- [Deploy](#deploy)
- [Contribuindo](#contribuindo)

## 🎯 Visão Geral

O **MS Orchestrator** implementa o padrão **Saga Pattern** para orquestrar transações distribuídas envolvendo múltiplos microsserviços. Ele atua como coordenador central, garantindo que os eventos sejam roteados corretamente entre os serviços e que o fluxo de negócio seja executado de forma consistente, mesmo em caso de falhas.

### Principais Funcionalidades

- ✅ Orquestração de eventos entre microsserviços
- ✅ Gerenciamento de transações distribuídas (Saga Pattern)
- ✅ Tratamento de rollback em caso de falhas
- ✅ Comunicação assíncrona via AWS SQS
- ✅ Histórico de eventos para rastreabilidade

## 🏗️ Arquitetura

O projeto segue os princípios da **Arquitetura Hexagonal (Ports & Adapters)**, separando a lógica de negócio da infraestrutura:

```
┌─────────────────────────────────────────────────────────┐
│                    Infrastructure                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │   Consumer   │  │   Producer   │  │  Controller  │   │
│  │    (SQS)     │  │    (SQS)     │  │   (REST)     │   │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┘   │
└─────────┼─────────────────┼─────────────────────────────┘
          │                 │
          ▼                 ▼
┌─────────────────────────────────────────────────────────┐
│                  Application Layer                      │
│  ┌──────────────────────────────────────────────────┐   │
│  │         OrchestrationUseCase (Port In)           │   │
│  │         MessageProducerPort (Port Out)           │   │
│  └──────────────────────────────────────────────────┘   │
└─────────┬───────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │    Event     │  │SagaHandler   │  │   Services   │   │
│  │    Order     │  │              │  │              │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────┘
```

## 🔄 Fluxo da Saga

O orquestrador gerencia o seguinte fluxo de transações:

### 1. Início da Saga (Fan-out)
Quando um pedido é criado, o orquestrador recebe um evento na fila `order-queue` e inicia a saga enviando eventos em paralelo para:
- `payment-queue` → Processamento de pagamento
- `production-queue` → Preparação da produção

### 2. Processamento de Pagamento
- **SUCCESS**: Envia evento para `production-queue` para iniciar a produção
- **FAIL**: Inicia rollback enviando evento para `production-queue` com status de falha
- **ROLLBACK_PENDING**: Notifica `payment-queue` e `production-queue` sobre o rollback pendente

### 3. Processamento de Produção
- **SUCCESS**: Finaliza a saga enviando evento para `order-callback-queue`
- **FAIL**: Finaliza a saga com erro enviando para `order-callback-queue`
- **ROLLBACK_PENDING**: Notifica `production-queue` e `payment-queue` sobre o rollback pendente

### Diagrama de Fluxo

```
┌─────────┐
│  Order  │
└────┬────┘
     │
     ▼
┌─────────────────┐
│  Orchestrator   │
│  (Start Saga)   │
└────┬──────┬─────┘
     │      │
     ▼      ▼
┌─────────┐ ┌──────────────┐
│ Payment │ │ Production   │
└────┬────┘ └──────┬───────┘
     │            │
     │ SUCCESS    │ SUCCESS
     ▼            ▼
┌─────────────────┐
│  Orchestrator   │
│  (Continue)     │
└────┬────────────┘
     │
     ▼
┌─────────────┐
│ Order       │
│ Callback    │
└─────────────┘
```

## 🛠️ Tecnologias

- **Java 17** - Linguagem base
- **Kotlin 2.2.21** - Linguagem principal
- **Spring Boot 4.0.1** - Framework
- **Spring Cloud AWS SQS 4.0.0-M1** - Integração com AWS SQS
- **Jackson** - Serialização JSON
- **Logstash Logback Encoder** - Logs estruturados em JSON
- **Maven** - Gerenciamento de dependências
- **Docker** - Containerização
- **LocalStack** - Emulação local da AWS
- **Cucumber** - Testes BDD
- **MockK** - Mocking para Kotlin
- **JaCoCo** - Cobertura de código

## 📦 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose (para ambiente local)
- AWS CLI (opcional, para testes locais)

## 🚀 Como Executar

### Ambiente Local com LocalStack

1. **Clone o repositório**
```bash
git clone https://github.com/Grupo-202-FIAP/ms-orchestrator
cd ms-orchestrator
```

2. **Inicie o LocalStack**
```bash
docker compose up -d orchestrator-localstack
```

3. **Crie as filas SQS no LocalStack**
```bash
chmod +x ./local/init-aws.sh
./local/init-aws.sh
```

Ou execute dentro do container:
```bash
docker exec orchestrator-localstack /bin/bash /etc/localstack/init/ready.d/init-aws.sh
```

4. **Execute a aplicação**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

A aplicação estará disponível em `http://localhost:8080`

### Executando com Docker Compose

```bash
docker compose up -d
```

Isso iniciará tanto o LocalStack quanto a aplicação.

### Executando os Testes

```bash
# Testes unitários e de integração
mvn clean verify

# Apenas testes unitários
mvn test

# Gerar relatório de cobertura
mvn clean verify
# Relatório disponível em: target/site/jacoco/index.html
```

## 📁 Estrutura do Projeto

```
ms-orchestrator/
├── src/
│   ├── main/
│   │   ├── kotlin/com/nextime/orchestrator/
│   │   │   ├── application/          # Camada de aplicação
│   │   │   │   ├── config/           # Configurações
│   │   │   │   ├── exception/        # Exceções da aplicação
│   │   │   │   ├── gateways/         # Portas de logging
│   │   │   │   ├── ports/            # Ports (in/out)
│   │   │   │   └── usecases/         # Casos de uso
│   │   │   ├── domain/               # Camada de domínio
│   │   │   │   ├── enums/            # Enumerações
│   │   │   │   ├── exception/        # Exceções de domínio
│   │   │   │   ├── services/        # Serviços de domínio
│   │   │   │   └── *.kt              # Entidades de domínio
│   │   │   ├── infrastructure/      # Camada de infraestrutura
│   │   │   │   ├── adapters/        # Adaptadores
│   │   │   │   ├── controller/      # Controllers REST
│   │   │   │   ├── exception/       # Exceções de infraestrutura
│   │   │   │   └── messaging/       # Consumidores e produtores
│   │   │   └── utils/               # Utilitários
│   │   └── resources/
│   │       ├── application-*.yaml    # Configurações por ambiente
│   │       └── logback-spring.xml   # Configuração de logs
│   └── test/
│       ├── kotlin/                  # Testes unitários
│       └── resources/
│           └── features/             # Features Cucumber (BDD)
├── infra/                           # Infraestrutura como código
│   ├── k8s/                         # Manifests Kubernetes
│   └── terraform/                   # Terraform para AWS
├── local/                           # Scripts locais
├── docker-compose.yml               # Configuração Docker Compose
├── Dockerfile                       # Imagem Docker
└── pom.xml                          # Dependências Maven
```

## 🧪 Testes

O projeto possui três tipos de testes:

### Testes Unitários
Testes isolados de componentes individuais usando MockK.

### Testes de Integração (BDD)
Testes de integração usando **Cucumber** com cenários escritos em Gherkin:

```gherkin
Funcionalidade: Orquestração de pedidos
  Cenário: Redirecionamento de eventos com sucesso
    Dado que um evento válido é enviado para a fila order_queue
    Quando o evento é recebido pelo orquestrador
    Então o orquestrador deve redirecionar o evento para 2 filas
```

### Executando Testes Específicos

```bash
# Apenas testes de integração
mvn test -Dtest=OrchestratorApplicationIntegrationTests

# Com perfil específico
mvn test -Dspring.profiles.active=test
```

## ⚙️ Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SPRING_PROFILES_ACTIVE` | Perfil ativo | `local` |
| `AWS_REGION` | Região AWS | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | Access Key AWS | - |
| `AWS_SECRET_ACCESS_KEY` | Secret Key AWS | - |
| `SQS_ORDER_QUEUE` | Nome da fila de pedidos | `order-queue` |
| `SQS_PAYMENT_QUEUE` | Nome da fila de pagamento | `payment-queue` |
| `SQS_PRODUCTION_QUEUE` | Nome da fila de produção | `production-queue` |

### Filas SQS

O orquestrador utiliza as seguintes filas:

- `order-queue` - Recebe eventos de novos pedidos
- `order-callback-queue` - Callback final do pedido
- `payment-queue` - Envia eventos para processamento de pagamento
- `payment-callback-queue` - Recebe callbacks de pagamento
- `production-queue` - Envia eventos para produção
- `production-callback-queue` - Recebe callbacks de produção

### Perfis de Ambiente

- **local**: Ambiente local com LocalStack
- **dev**: Ambiente de desenvolvimento
- **test**: Ambiente de testes

## 🚢 Deploy

### Kubernetes

O projeto inclui manifests Kubernetes em `infra/k8s/`:

```bash
kubectl apply -f infra/k8s/
```

### Terraform

Infraestrutura AWS provisionada via Terraform em `infra/terraform/`:

```bash
cd infra/terraform
terraform init
terraform plan
terraform apply
```

### CI/CD

O projeto possui workflows GitHub Actions:

- **CI Feature**: Testes e build para branches de feature
- **CI Dev**: Testes e PR para branch `dev`
- **CI Hom**: Testes e build para branch `hom`
- **CD Main**: Deploy para produção

## 📝 Scripts Úteis

### Publicar Mensagens de Teste

```bash
# Publicar evento de sucesso de pagamento
./local/publish-payment-success.sh

# Publicar evento de falha de pagamento
./local/publish-payment-failed.sh

# Publicar evento de sucesso de produção
./local/publish-production-success.sh

# Limpar todas as filas
./local/purge-queues.sh
```

## 🤝 Contribuindo

1. Crie uma branch a partir de `dev`
2. Faça suas alterações
3. Execute os testes: `mvn clean verify`
4. Certifique-se de que a cobertura de código está adequada
5. Abra um Pull Request para `dev`

### Padrões de Código

- Siga os princípios da Arquitetura Hexagonal
- Mantenha a separação de responsabilidades
- Escreva testes para novas funcionalidades
- Use Kotlin idioms e best practices
- Documente código complexo

## 📄 Licença

Este projeto é proprietário da Nextime.

## 👥 Autores

Equipe Nextime

---

**Nota**: Este é um microsserviço crítico para o fluxo de pedidos. Sempre teste localmente antes de fazer deploy e certifique-se de que todas as filas SQS estão configuradas corretamente.
