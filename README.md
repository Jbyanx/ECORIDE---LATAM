# ECO-RIDE LATAM - Carpooling Corporativo

> Estado del Proyecto: En Desarrollo (Fase 4: Saga Implementada)
> Stack: Java 21 | Spring Boot 3.3.5 | Spring Cloud 2023.0.3

Eco-Ride LATAM es una plataforma de microservicios diseñada para optimizar la movilidad corporativa en América Latina.

---

## Arquitectura del Sistema

El sistema implementa una arquitectura de microservicios reactiva y orientada a eventos.

### Componentes Principales

| Servicio           | Puerto | Descripción                                      |
| :----------------- | :----- | :----------------------------------------------- |
| Config Server      | 8888   | Gestión centralizada de configuración.           |
| Eureka Server      | 8761   | Registro y descubrimiento de servicios.          |
| API Gateway        | 8090   | Puerta de enlace, enrutamiento y seguridad.      |
| Trip Service       | 8081   | Gestión de viajes y Orquestador de Sagas.        |
| Passenger Service  | 8082   | Gestión de perfiles y pasajeros.                 |
| Payment Service    | 8083   | Procesamiento de pagos (Consumidor de Eventos).  |
| Notification Svc   | 8084   | Envío de alertas. (Pendiente)                    |

### Infraestructura (Docker)

- PostgreSQL: Bases de datos aisladas por servicio (Puertos 5432 mapeados a 543x).
- RabbitMQ: Broker de mensajería para la coreografía de Sagas (5672).
- Keycloak: Servidor de Identidad OIDC/OAuth2 (8080).
- Observabilidad: Grafana, Prometheus, Tempo (Tracing Distribuido).

---

## Guía de Inicio Rápido

1. Prerrequisitos:
   - Java 21, Maven 3.8+, Docker, Git.

2. Clonar repositorio:
   git clone https://github.com/tu-usuario/ecoride-latam.git

3. Levantar Infraestructura Base:
   docker-compose up -d

4. Configuración Manual de Seguridad (Keycloak):
   - Entrar a http://localhost:8080 (admin/admin).
   - Crear Realm: 'ecoride'.
   - Crear Cliente: 'eco-gateway-client' (Confidential, Service Accounts Enabled).
   - Configurar Redirect URIs: http://localhost:8090/*
   - Copiar Secret y ponerlo en 'config-data/gateway.yml'.
   - Crear Rol: 'ROLE_DRIVER'.
   - Crear Usuario: 'driver1'.

5. Ejecutar Servicios (Orden sugerido):
   1. Config Server
   2. Eureka Server
   3. Payment Service & Trip Service & Passenger Service
   4. API Gateway

---

## Pruebas de Endpoints

### 1. Crear Viaje (Conductor)
URL: http://localhost:8090/api/trips
Método: POST
Auth: Login Keycloak (driver1)

Body:
{
"origin": "Centro",
"destination": "Norte",
"departureTime": "2025-12-01T08:00:00",
"seatsAvailable": 4,
"price": 15000.00
}

### 2. Ver Perfil (Pasajero/Conductor)
URL: http://localhost:8090/api/passengers/me
Método: GET
Nota: Crea el perfil automáticamente si no existe.

### 3. Reservar Viaje (Inicia Saga de Pago)
URL: http://localhost:8090/api/trips/{tripId}/reserve
Método: POST
Auth: Login Keycloak

Flujo de la Saga (Coreografía):
1. TripService crea evento RESERVATION_REQUESTED -> RabbitMQ.
2. PaymentService consume evento, procesa pago y emite PAYMENT_PROCESSED -> RabbitMQ.
3. TripService consume resultado y actualiza estado (CONFIRMED/CANCELLED).

---

## Progreso

[x] Fase 1: Infraestructura (Docker, Config, Eureka).
[x] Fase 2: Seguridad (Keycloak, Gateway Token Relay).
[x] Fase 3: Persistencia (TripService, PassengerService).
[x] Fase 4: Sagas (Coreografía Trip <-> Payment con RabbitMQ).
[ ] Fase 5: Notificaciones (Email).

Curso: Microservicios