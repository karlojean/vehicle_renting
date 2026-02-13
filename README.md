# 🚗 Vehicle Renting API

API REST para gerenciamento de aluguel de veículos, desenvolvida com Spring Boot.

O sistema conecta **parceiros** (donos de veículos) a **clientes** (locatários), permitindo cadastro de veículos, reservas com validação de disponibilidade, inspeções de retirada/devolução e upload de mídias via S3.

## 📋 Funcionalidades

### Autenticação

- Registro de contas (RENTER / PARTNER)
- Login com JWT (stateless)

### Veículos (Partner)

- CRUD completo de veículos
- Ativação / Desativação de veículos
- Upload e remoção de imagens (S3/MinIO)
- Busca pública de veículos disponíveis por cidade e período

### Reservas (Bookings)

- Criação de reserva com validação de sobreposição de datas
- Confirmação / Cancelamento pelo parceiro
- Listagem de reservas recebidas (Partner) e solicitadas (Renter)

### Inspeções

- Inspeção de retirada (PICK_UP) e devolução (DROP_OFF)
- Atualização de checklist (odômetro, combustível, limpeza, etc.)
- Conclusão da inspeção transiciona o status do booking automaticamente:
  - PICK_UP concluída → Booking vira **ACTIVE**
  - DROP_OFF concluída → Booking vira **COMPLETED**
- Upload de mídias por inspeção

### Localizações

- Cadastro de locais dos parceiros
- Listagem por parceiro

## 🛠️ Principal Stack

| Tecnologia | Versão |
|---|---|
| Java | 25 |
| Spring Boot | 4.0 |
| PostgreSQL | 18 |

## 🚀 Como Rodar

### Pré-requisitos

- Java 25+
- Docker & Docker Compose
- Maven (ou usar o `./mvnw` incluso)

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/vehicle-renting.git
cd vehicle-renting
```

### 2. Configure as variáveis de ambiente

```bash
cp .env.example .env
# Edite o .env se necessário (os defaults já funcionam para dev)
```

### 3. Suba os containers (PostgreSQL + MinIO)

```bash
docker compose up -d
```

### 4. Rode a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080/api/v1`

### 5. Acesse o MinIO Console (opcional)

- URL: `http://localhost:9001`
- User: `dev-access-key`
- Password: `dev-secret-key`

## 🧪 Testes

Os testes de integração usam **Testcontainers** (sobe um PostgreSQL automaticamente) e **RestAssured**.

```bash
./mvnw test
```

> ⚠️ Docker precisa estar rodando para os testes de integração.

## 📁 Estrutura do Projeto

```
src/main/java/dev/jeankarlo/vehiclerenting/
├── config/
│   ├── S3/                  # Configuração S3 + BucketType enum
│   └── security/            # SecurityConfig, JwtFilter, UserDetailsService
├── controller/
│   ├── handlers/            # @RestControllerAdvice global
│   ├── AuthController
│   ├── AccountController
│   ├── VehicleController
│   ├── BookingController
│   ├── InspectionController
│   └── LocationController
├── dto/                     # Records separados por domínio (Request/Response)
├── entity/                  # Entidades JPA + enums
├── exception/               # BusinessException + especializações
├── mapper/                  # MapStruct mappers
├── repository/              # Spring Data JPA repositories
├── service/                 # Interfaces
│   └── impl/                # Implementações
├── specifications/          # JPA Specifications (busca dinâmica)
└── utils/                   # JwtTokenUtil

src/main/resources/
├── db/migration/            # Flyway SQL scripts
│   ├── V1__initial_schema.sql
│   ├── V2__booking_constraint.sql
│   └── V3__media_asset.sql
├── application.yml
├── application-dev.yml
└── application-test.yml
```

## 📌 Endpoints Principais

### Auth

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/auth/register` | Registrar conta | Público |
| POST | `/auth/login` | Login (retorna JWT) | Público |

### Vehicles

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/vehicles/available` | Buscar veículos disponíveis | Público |
| POST | `/vehicles` | Criar veículo | PARTNER |
| GET | `/vehicles` | Listar meus veículos | PARTNER |
| GET | `/vehicles/{id}` | Detalhes do veículo | PARTNER |
| PATCH | `/vehicles/{id}` | Atualizar veículo | PARTNER |
| DELETE | `/vehicles/{id}` | Remover veículo | PARTNER |
| PATCH | `/vehicles/{id}/activate` | Ativar veículo | PARTNER |
| PATCH | `/vehicles/{id}/deactivate` | Desativar veículo | PARTNER |
| POST | `/vehicles/{id}/images` | Upload de imagem | PARTNER |
| GET | `/vehicles/{id}/images` | Listar imagens | Autenticado |
| DELETE | `/vehicles/{id}/images/{mediaId}` | Remover imagem | PARTNER |

### Bookings

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/bookings` | Criar reserva | Autenticado |
| GET | `/bookings/received` | Reservas recebidas | PARTNER |
| GET | `/bookings/my-requests` | Minhas reservas | RENTER |
| PATCH | `/bookings/{id}/confirm` | Confirmar reserva | PARTNER |
| PATCH | `/bookings/{id}/cancel` | Cancelar reserva | PARTNER |

### Inspections

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/inspections` | Iniciar inspeção | PARTNER |
| PATCH | `/inspections/{id}` | Atualizar checklist | PARTNER |
| PATCH | `/inspections/{id}/complete` | Concluir inspeção | PARTNER |
| PATCH | `/inspections/{id}/cancel` | Cancelar inspeção | PARTNER |
| GET | `/inspections?bookingId=` | Listar por booking | PARTNER |
| POST | `/inspections/{id}/medias` | Upload de mídia | PARTNER |
| GET | `/inspections/{id}/medias` | Listar mídias | PARTNER |
| DELETE | `/inspections/{id}/medias/{mediaId}` | Remover mídia | PARTNER |

### Locations

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/locations` | Criar localização | PARTNER |
| GET | `/locations` | Listar minhas localizações | PARTNER |

### Account

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/accounts/me` | Dados da conta logada | Autenticado |
