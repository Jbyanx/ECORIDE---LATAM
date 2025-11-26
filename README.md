# ECO-RIDE LATAM - Carpooling Corporativo

> Estado del Proyecto: Completado (Fase 1 a 5)
> Stack: Java 21 | Spring Boot 3.3.5 | Spring Cloud 2023.0.3

Eco-Ride LATAM es una plataforma de microservicios distribuida diseñada para optimizar la movilidad corporativa. Implementa patrones avanzados de arquitectura reactiva, seguridad y consistencia eventual.

---

## Arquitectura del Sistema

El sistema utiliza una arquitectura dirigida por eventos (Event-Driven) para garantizar el desacoplamiento y la escalabilidad.

### Microservicios Implementados

| Servicio           | Puerto | Descripción                                      |
| :----------------- | :----- | :----------------------------------------------- |
| Config Server      | 8888   | Gestión centralizada de configuración (Native).  |
| Eureka Server      | 8761   | Registro y descubrimiento de servicios (SD).     |
| API Gateway        | 8090   | Puerta de enlace, seguridad (OAuth2) y Routing.  |
| Trip Service       | 8081   | Gestión de viajes y Orquestador de Sagas.        |
| Passenger Service  | 8082   | Gestión de perfiles y autenticación implícita.   |
| Payment Service    | 8083   | Procesamiento de pagos y validación financiera.  |
| Notification Svc   | 8084   | Envío de correos transaccionales (Async).        |

### Infraestructura (Docker)

- PostgreSQL (x4): Bases de datos aisladas (Pattern: DB per Service).
- RabbitMQ: Broker de mensajería para la coreografía de Sagas.
- Keycloak: Servidor de Identidad (IAM) OIDC/OAuth2.
- Observabilidad: Grafana, Prometheus, Tempo (Tracing Distribuido).

---

## Características Técnicas Destacadas

1. **Patrón Saga (Coreografía):** Gestión de transacciones distribuidas entre Trip y Payment sin bloqueo de recursos. Incluye flujos de éxito (Confirmación) y fallo (Compensación/Rollback).
2. **Resiliencia (Retry Pattern):** El consumidor de eventos en TripService implementa reintentos inteligentes para mitigar condiciones de carrera (Race Conditions) por latencia de base de datos.
3. **Seguridad Perimetral:** El Gateway implementa un filtro reactivo (`AuthenticationFilter`) que decodifica el JWT de Keycloak e inyecta la identidad (`X-User-Id`) a los servicios internos de forma transparente.
4. **Notificaciones Asíncronas:** NotificationService escucha múltiples colas para enviar alertas en tiempo real sin afectar el rendimiento de las transacciones.

---

## Guía de Despliegue Local

### 1. Prerrequisitos
- Java 21, Maven 3.8+, Docker, Git.

### 2. Clonar e Iniciar Infraestructura
git clone https://github.com/tu-usuario/ecoride-latam.git
docker-compose up -d

### 3. Configuración de Seguridad (Keycloak)
- Acceder a http://localhost:8080 (admin/admin).
- Crear Realm: 'ecoride'.
- Crear Cliente: 'eco-gateway-client' (Confidential, Service Accounts Enabled).
- Redirect URIs: http://localhost:8090/*
- Actualizar 'client-secret' en 'config-data/gateway.yml'.
- Crear Roles: 'ROLE_DRIVER', 'ROLE_PASSENGER'.
- Crear Usuario: 'driver1'.

### 4. Ejecución de Servicios
Orden estricto de arranque:
1. Config Server (8888)
2. Eureka Server (8761)
3. Payment, Notification, Passenger, Trip Services.
4. API Gateway (8090)

---

## Escenarios de Prueba (Saga)

### Escenario 1: Happy Path (Pago Exitoso)
Crea una reserva con un monto estándar.
- **Acción:** `POST /api/trips/{id}/reservations` (Monto implícito: < $50,000)
- **Flujo:** Trip (PENDING) -> RabbitMQ -> Payment (AUTHORIZED) -> RabbitMQ -> Trip (CONFIRMED) -> Notification (Email Éxito).

### Escenario 2: Unhappy Path (Fallo y Compensación)
Intenta reservar un viaje con precio alto (simulado > $50,000).
- **Acción:** Crear viaje con `price: 100000` y reservar.
- **Flujo:** Trip (PENDING) -> Payment (FAILED - Saldo Insuficiente) -> RabbitMQ -> Trip (CANCELLED - Libera Asiento) -> Notification (Email Fallo).

---

## Endpoints Principales

| Método | Endpoint | Descripción | Auth |
| :--- | :--- | :--- | :--- |
| POST | `/api/trips` | Publicar nuevo viaje | Driver |
| GET | `/api/trips` | Buscar viajes disponibles | Public/Auth |
| POST | `/api/trips/{id}/reservations` | Reservar asiento (Inicia Saga) | Passenger |
| GET | `/api/passengers/me` | Ver perfil de usuario | Auth |

---

## Progreso del Proyecto

- [x] Fase 1: Infraestructura (Docker, Config, Eureka).
- [x] Fase 2: Seguridad (Keycloak, Gateway Token Relay).
- [x] Fase 3: Persistencia (Arquitectura Hexagonal/Capas).
- [x] Fase 4: Sagas (Coreografía Completa + Compensación + Retry).
- [x] Fase 5: Notificaciones (Email Service).

Curso: Microservicios
