#!/bin/bash

# Verifica se o nome da fila foi fornecido
if [ $# -ne 1 ]; then
  echo "Uso: $0 <nome_da_fila>"
  exit 1
fi
echo "SQS Step 1"
# Nome da fila
queue_name="$1"
echo "SQS Step 2 - $1"

# Obtém a URL da fila recém-criada
queue_url=$(aws sqs get-queue-url --queue-name "$queue_name" --query QueueUrl --output text)
echo "SQS Step 3"

# Verifica se a fila existe
if [ -z "$queue_url" ]; then
  echo "SQS Step 4"
  # Comando para criar a fila
  aws sqs create-queue --queue-name "$queue_name"
  echo "SQS Step 5"
  # Obtém a URL da fila recém-criada
  queue_url=$(aws sqs get-queue-url --queue-name "$queue_name" --query QueueUrl --output text)
  echo "SQS Step 6"
  # Verifica se a fila foi criada com sucesso
  if [ -z "$queue_url" ]; then
    echo "Erro ao criar a fila"
    exit 1
  fi
fi
echo "SQS Step 7"
endpoint=$(echo "$queue_url" | sed 's/\(https:\/\/.*aws\.com\).*/\1/')

# Imprime a URL da fila
echo "QUEUE=$queue_url"
echo "SQS_ENDPOINT=$endpoint"
