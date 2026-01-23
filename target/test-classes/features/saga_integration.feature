# language: pt

Funcionalidade: Orquestração de pedidos - Integração de pagamento, pedido e produção

  Esquema do Cenario: Redirecionamento de eventos com sucesso
    Dado que um evento válido é enviado para a fila <queue_name> com o status <status> e o source <source>
    Quando o evento é recebido pelo orquestrador
    Entao o orquestrador deve redirecionar o evento para <quantity_queues> filas de redirecionamento
    E o evento deve ser redirecionado para as filas <queue1> e <queue2>
    Exemplos:
      | queue_name                | status  | source     | quantity_queues | queue1               | queue2           |
      | order_queue               | null    | null       | 2               | payment_queue        | production_queue |
      | payment_callback_queue    | SUCCESS | PAYMENT    | 1               | production_queue     | null             |
      | production_callback_queue | SUCCESS | PRODUCTION | 1               | order_callback_queue | null             |

  Esquema do Cenario: Redirecionamento de eventos com falha
    Dado que um evento válido é enviado para a fila <queue_name> com o status <status> e o source <source>
    Quando o evento é recebido pelo orquestrador
    Entao o orquestrador deve redirecionar o evento para <quantity_queues> filas de redirecionamento
    E o evento deve ser redirecionado para as filas <queue1>
    Exemplos:
      | queue_name                | status | source     | quantity_queues | queue1               |
      | payment_callback_queue    | FAIL   | PAYMENT    | 1               | production_queue     |
      | production_callback_queue | FAIL   | PRODUCTION | 1               | order_callback_queue |

  Esquema do Cenario: Redirecionamento de eventos com rollback pending
    Dado que um evento válido é enviado para a fila <queue_name> com o status <status> e o source <source>
    Quando o evento é recebido pelo orquestrador
    Entao o orquestrador deve redirecionar o evento para <quantity_queues> filas de redirecionamento
    E o evento deve ser redirecionado para as filas <queue1> e <queue2>
    Exemplos:
      | queue_name       | status           | source     | quantity_queues | queue1           | queue2           |
      | payment_queue    | ROLLBACK_PENDING | PAYMENT    | 2               | production_queue | payment_queue    |
      | production_queue | ROLLBACK_PENDING | PRODUCTION | 2               | payment_queue    | production_queue |


