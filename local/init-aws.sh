#!/bin/bash
set -e

echo "########### Criando filas SQS no LocalStack ###########"

AWS_REGION="us-east-1"
ENDPOINT_URL="http://localhost:4566"

QUEUES=(
  "order-queue"
  "order-callback-queue"
  "production-queue"
  "production-callback-queue"
  "payment-queue"
  "payment-callback-queue"
)

for QUEUE_NAME in "${QUEUES[@]}"; do
  echo "Criando fila: ${QUEUE_NAME}"

  aws --endpoint-url="${ENDPOINT_URL}" sqs create-queue \
    --queue-name "${QUEUE_NAME}" \
    --region "${AWS_REGION}"
done

echo "########### Filas criadas com sucesso ###########"

echo "########### Listando filas SQS ###########"
aws --endpoint-url="${ENDPOINT_URL}" sqs list-queues --region "${AWS_REGION}"
