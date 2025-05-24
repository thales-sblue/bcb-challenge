#!/usr/bin/env bash
set -e

echo "⏳ Aguardando Postgres..."
./wait-for-it.sh postgres:5432 --strict --timeout=60 -- echo "✅ Postgres disponível"

echo "⏳ Aguardando RabbitMQ..."
./wait-for-it.sh rabbitmq:5672 --strict --timeout=60 -- echo "✅ RabbitMQ disponível"

exec java -jar app.jar
