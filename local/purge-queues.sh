#!/bin/bash
set -e

echo "########### Limpando filas SQS no LocalStack ###########"

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
  echo "Obtendo URL da fila: ${QUEUE_NAME}"

  QUEUE_URL=$(aws --endpoint-url="${ENDPOINT_URL}" sqs get-queue-url \
    --queue-name "${QUEUE_NAME}" \
    --region "${AWS_REGION}" \
    --query "QueueUrl" \
    --output text)

  echo "Purgando fila: ${QUEUE_NAME}"

  aws --endpoint-url="${ENDPOINT_URL}" sqs purge-queue \
    --queue-url "${QUEUE_URL}" \
    --region "${AWS_REGION}"
done

echo "########### Todas as filas foram purgadas com sucesso ###########"
