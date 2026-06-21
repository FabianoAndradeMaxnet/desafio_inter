# Todo Senior Challenge

Aplicação full stack para gerenciamento de tarefas, construída com backend Micronaut Java 17, persistência PostgreSQL, mensageria Kafka, migrations Flyway e frontend React + TypeScript + Vite.

O projeto foi estruturado para demonstrar separação clara de responsabilidades, arquitetura em camadas, domínio isolado de infraestrutura, testes automatizados e execução local reprodutível com Docker.

## Arquitetura

A aplicação segue uma arquitetura em camadas:

- `api`: camada de entrada HTTP. Contém controllers REST, DTOs de request/response, mappers HTTP, validações e handlers globais de exceção.
- `application`: orquestra os casos de uso. Contém serviços/use cases, comandos de entrada e portas de saída como publisher de eventos.
- `domain`: núcleo da regra de negócio. Contém entidades, enums, validações de domínio, paginação e contratos como `TaskRepository`.
- `infrastructure`: implementações técnicas. Contém JPA/Hibernate, repositórios Micronaut Data, mappers de persistência, Kafka publisher/consumer e integrações externas.
- `frontend`: aplicação React + TypeScript consumindo a API REST versionada.

Fluxo principal:

1. O frontend chama a API `/api/v1/tasks`.
2. O controller valida e converte requests para comandos de aplicação.
3. O use case executa regra de negócio usando contratos do domínio.
4. A infraestrutura persiste em PostgreSQL via JPA/Hibernate.
5. Eventos de aplicação são publicados via porta `TaskEventPublisher`.
6. A implementação Kafka publica e consome eventos no tópico `task-events`.

## Decisões Técnicas

- **Micronaut**: escolhido por inicialização rápida, baixo overhead e boa integração com DI, validação, JPA, Kafka, OpenAPI e testes.
- **Java 17**: versão LTS moderna, estável e adequada para aplicações enterprise.
- **Arquitetura em camadas**: domínio não depende de detalhes HTTP, banco ou Kafka.
- **Separação entre domínio e persistência**: `Task` é entidade de domínio; `TaskEntity` é entidade JPA.
- **Use cases explícitos**: criação, atualização, alteração de status, listagem e exclusão ficam em classes dedicadas.
- **Flyway**: versionamento de schema previsível e rastreável.
- **PostgreSQL**: banco relacional robusto, com suporte nativo a UUID e timestamps.
- **Kafka desacoplado**: eventos são expostos por portas de aplicação; Kafka é detalhe de infraestrutura.
- **DTOs e mappers**: evitam vazamento de modelos internos para a API.
- **Respostas padronizadas**: sucesso e erro seguem contratos consistentes.
- **Testcontainers**: testes de integração executam com PostgreSQL e Kafka reais.
- **React + TypeScript + Vite**: frontend tipado, leve e rápido para desenvolvimento.
- **GitHub Actions**: pipeline valida backend e frontend em jobs separados.

## Estrutura do Projeto

```text
.
├── .github/workflows/
│   └── ci.yml
├── docker-compose.yml
├── Dockerfile
├── Dockerfile.backend
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/example/todo/
│   │   │   ├── api/
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   └── infrastructure/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── db/migration/
│   │       └── logback.xml
│   └── test/
│       └── java/com/example/todo/
└── frontend/
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── components/
        ├── services/
        ├── types/
        ├── App.tsx
        └── main.tsx
```

## Requisitos

- Java 17+
- Maven 3.9+
- Docker e Docker Compose
- Node.js 22+
- npm

## Como Executar com Docker

Copie as variáveis de ambiente:

```bash
cp .env.example .env
```

Suba a stack:

```bash
docker compose up --build
```

Serviços:

- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- Kafka externo: `localhost:9094`
- Healthcheck: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui`

O `docker-compose.yml` orquestra backend, PostgreSQL e Kafka. Para usar a interface web em modo de desenvolvimento, execute o frontend separadamente:

```bash
cd frontend
npm install
npm run dev
```

Frontend local:

```text
http://localhost:5173
```

Portas podem ser sobrescritas:

```bash
BACKEND_PORT=8081 POSTGRES_PORT=55432 KAFKA_PORT=19094 docker compose up --build
```

## Como Executar Localmente

Suba apenas dependências:

```bash
docker compose up postgres kafka
```

Confirme que os serviços estão saudáveis:

```bash
docker compose ps
```

Execute o backend:

```bash
mvn mn:run
```

Se usar portas alternativas:

```bash
POSTGRES_PORT=55432 KAFKA_PORT=19094 docker compose up postgres kafka

JDBC_URL=jdbc:postgresql://localhost:55432/todo \
KAFKA_BOOTSTRAP_SERVERS=localhost:19094 \
mvn mn:run
```

Execute o frontend:

```bash
cd frontend
npm install
npm run dev
```

Frontend local:

```text
http://localhost:5173
```

O Vite possui proxy para `/api` apontando para `http://localhost:8080`. Para apontar para outro host, defina:

```bash
VITE_API_BASE_URL=http://localhost:8081 npm run dev
```

## Problemas Comuns

### Porta 5432 em uso

Se o PostgreSQL não subir com erro semelhante a `Bind for 0.0.0.0:5432 failed: port is already allocated`, já existe outro processo ou container usando a porta `5432`.

Verifique os containers ativos:

```bash
docker ps
```

Pare o container que está ocupando a porta, se ele não for necessário:

```bash
docker stop dev-postgres
docker compose up postgres kafka
```

Ou rode o PostgreSQL do projeto em outra porta:

```bash
POSTGRES_PORT=55432 docker compose up postgres kafka
```

Nesse caso, execute o backend apontando para a porta alternativa:

```bash
JDBC_URL=jdbc:postgresql://localhost:55432/todo \
KAFKA_BOOTSTRAP_SERVERS=localhost:9094 \
mvn mn:run
```

### Conexão recusada na porta 5432

Se `mvn mn:run` indicar que a conexão com `localhost:5432` foi recusada, confirme se o PostgreSQL está ativo e com porta publicada:

```bash
docker compose ps postgres
```

A saída deve mostrar algo como:

```text
0.0.0.0:5432->5432/tcp
```

Se o container estiver ativo sem essa publicação de porta, recrie o PostgreSQL:

```bash
docker compose stop postgres
docker compose rm -f postgres
docker compose up -d postgres kafka
```

## Docker

O projeto contém:

- `Dockerfile.backend`: build multi-stage do backend Java/Maven.
- `Dockerfile`: alias/compatibilidade para build do backend.
- `docker-compose.yml`: orquestra backend, PostgreSQL e Kafka.

Variáveis principais:

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `BACKEND_PORT` | `8080` | Porta HTTP exposta pelo backend |
| `POSTGRES_DB` | `todo` | Nome do banco |
| `POSTGRES_USER` | `todo` | Usuário do banco |
| `POSTGRES_PASSWORD` | `todo` | Senha do banco |
| `POSTGRES_PORT` | `5432` | Porta local do PostgreSQL |
| `KAFKA_PORT` | `9094` | Porta local externa do Kafka |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | Bootstrap Kafka usado pelo backend no Compose |
| `TASK_EVENTS_TOPIC` | `task-events` | Tópico de eventos de tarefas |
| `TASK_EVENTS_GROUP_ID` | `todo-task-events` | Consumer group do handler de eventos |

## Endpoints

Base URL:

```text
/api/v1
```

### Criar tarefa

```http
POST /api/v1/tasks
Content-Type: application/json
```

```json
{
  "title": "Primeira tarefa",
  "description": "Descrição opcional"
}
```

### Listar tarefas

```http
GET /api/v1/tasks?page=0&size=20
```

Resposta paginada:

```json
{
  "data": [
    {
      "id": "uuid",
      "title": "Primeira tarefa",
      "description": "Descrição opcional",
      "status": "TODO",
      "createdAt": "2026-06-11T22:00:00Z",
      "updatedAt": "2026-06-11T22:00:00Z"
    }
  ],
  "meta": {
    "totalItems": 1,
    "page": 0,
    "size": 20,
    "totalPages": 1
  },
  "error": null
}
```

### Atualizar tarefa

```http
PUT /api/v1/tasks/{id}
Content-Type: application/json
```

```json
{
  "title": "Tarefa atualizada",
  "description": "Nova descrição"
}
```

### Atualizar status

```http
PATCH /api/v1/tasks/{id}/status
Content-Type: application/json
```

```json
{
  "status": "IN_PROGRESS"
}
```

Status disponíveis:

- `TODO`
- `IN_PROGRESS`
- `DONE`
- `CANCELLED`

### Remover tarefa

```http
DELETE /api/v1/tasks/{id}
```

### Healthcheck

```http
GET /actuator/health
```

### Eventos em tempo real

```http
GET /api/v1/tasks/events
Accept: text/event-stream
```

Esse endpoint usa Server-Sent Events (SSE) para retransmitir aos browsers conectados os eventos consumidos do Kafka.

### OpenAPI / Swagger

```text
http://localhost:8080/swagger-ui
http://localhost:8080/swagger/todo-senior-challenge-api-0.1.0.yml
```

## Eventos

A aplicação publica eventos no Kafka no tópico `task-events`.

Eventos implementados:

- `TASK_CREATED`
- `TASK_STATUS_UPDATED`

O domínio não conhece Kafka. A aplicação usa a porta `TaskEventPublisher`, e a infraestrutura implementa essa porta com Kafka. Quando o consumidor Kafka recebe `TASK_CREATED` ou `TASK_STATUS_UPDATED`, ele também publica o evento em um hub interno de tempo real. O endpoint SSE `/api/v1/tasks/events` retransmite esses eventos para os browsers conectados.

Fluxo em tempo real:

1. O frontend cria ou altera o status de uma tarefa.
2. A API persiste a alteração no PostgreSQL.
3. A aplicação publica um evento no Kafka.
4. O consumidor Kafka processa o evento.
5. O evento é enviado por SSE aos browsers conectados.
6. Cada browser recarrega a lista de tarefas automaticamente.

Para testar a sincronização, abra `http://localhost:5173` em duas abas ou dois browsers. Crie uma tarefa ou altere o status em uma aba; a outra deve atualizar a lista automaticamente.

Também é possível observar o stream diretamente:

```bash
curl -N http://localhost:8080/api/v1/tasks/events
```

## Testes

O projeto possui:

- Testes unitários de domínio.
- Testes unitários dos use cases com fakes em memória.
- Teste de integração cobrindo o fluxo principal com Testcontainers:
  - PostgreSQL real
  - Kafka real
  - Flyway migrations
  - API REST
  - publicação e consumo de eventos

Executar testes:

```bash
mvn test
```

Executar verificação completa do backend:

```bash
mvn verify
```

Executar build do frontend:

```bash
cd frontend
npm ci
npm run build
```

## Pipeline CI

O workflow está em:

```text
.github/workflows/ci.yml
```

Jobs:

- `Backend Maven Verify`: executa `mvn verify` com Java 17 e Docker disponível para Testcontainers.
- `Frontend Build`: executa `npm ci` e `npm run build` com Node.js 22.

## Uso de IA

Este projeto foi desenvolvido com apoio de IA generativa durante a implementação. O uso foi direcionado para:

- scaffold inicial do projeto;
- implementação das camadas backend;
- criação de DTOs, mappers, validações e tratamento de exceções;
- integração com PostgreSQL, Flyway, JPA/Hibernate e Kafka;
- criação do frontend React + TypeScript;
- testes unitários e de integração com Testcontainers;
- pipeline GitHub Actions;
- documentação técnica.

As decisões arquiteturais, organização final, comandos executados e validações foram conduzidos de forma iterativa, com revisão e execução local dos testes/builds.

## Comandos Úteis

```bash
# Backend
mvn mn:run
mvn test
mvn verify

# Frontend
cd frontend
npm install
npm run dev
npm run build

# Docker
docker compose up --build
docker compose down
```
