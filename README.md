# BCB API – Backend Challenge

API para gestão de clientes, conversas e envio de mensagens com processamento assíncrono, controle de saldo/limite, priorização de mensagens e autenticação via JWT.

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- RabbitMQ
- Swagger (OpenAPI)
- Docker + Docker Compose

## Instruções de instalação e execução

Clone o projeto:

git clone https://github.com/thales-sblue/bcb-challenge.git
cd bcb-challenge

Rode com Docker:

docker-compose up --build

Acesse:

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- RabbitMQ: http://localhost:15672 (admin/admin123)

## Funcionalidades implementadas

- Autenticação com JWT, stateless e segura.
- CRUD completo de clientes, incluindo saldo e limite por plano (pré e pós-pago).
- Gerenciamento de conversas, com contador de não lidas e histórico de mensagens.
- Envio de mensagens com priorização (Normal e Urgente), processamento assíncrono via RabbitMQ.
- Controle financeiro automático:
  - Débito de saldo (pré-pago)
  - Controle de limite (pós-pago)
- Endpoint de monitoramento da fila (/queue/status) com status em tempo real.
- Tratamento de exceções estruturado:
  - Validações de entrada (DTOs)
  - Violação de regras de negócio (ex.: saldo insuficiente)
  - Retornos padronizados de erro no Swagger e na API.
- Documentação completa no Swagger, protegida via JWT.

## Decisões técnicas e limitações

- API desenhada 100% stateless, segura e escalável.
- Uso de RabbitMQ para garantir um fluxo de mensagens assíncrono e priorizado.
- Tratamento robusto de erros, tanto técnicos quanto de negócio.
- Sem implementação de Dead-letter ou Retries (possível evolução futura).

## Docker-compose incluso

Banco, API e RabbitMQ sobem juntos. Basta rodar:

docker-compose up --build


