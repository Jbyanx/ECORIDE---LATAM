# ECO-RIDE LATAM - Carpooling Corporativo

> Estado del Proyecto: En Desarrollo (Fase 2 Completada)
> Stack: Java 21 | Spring Boot 3.3.5 | Spring Cloud 2023.0.3

Eco-Ride LATAM es una plataforma de microservicios diseñada para optimizar la movilidad corporativa en América Latina.

---

## Arquitectura del Sistema

### Componentes Principales

| Servicio           | Puerto | Descripción                                      |
| :----------------- | :----- | :----------------------------------------------- |
| Config Server      | 8888   | Gestión centralizada de configuración.           |
| Eureka Server      | 8761   | Registro y descubrimiento de servicios.          |
| API Gateway        | 8090   | Puerta de enlace, enrutamiento y seguridad.      |
| Trip Service       | 8081   | Gestión de viajes (PostgreSQL).                  |
| Passenger Service  | 8082   | Gestión de perfiles. (Pendiente)                 |
| Payment Service    | 8083   | Procesamiento de pagos. (Pendiente)              |
| Notification Svc   | 8084   | Envío de alertas. (Pendiente)                    |

### Infraestructura (Docker)

- PostgreSQL: Puertos externos 5438 (Trip), 5433 (Passenger), etc.
- RabbitMQ: Puerto 5672 (Broker), 15672 (Admin).
- Keycloak: Puerto 8080 (IAM).
- Observabilidad: Grafana (3000), Prometheus (9090), Tempo.

---

## Guía de Inicio Rápido

1. Prerrequisitos:
    - Java 21, Maven, Docker, Git.

2. Clonar repositorio:
   git clone https://github.com/Jbyanx/ECORIDE---LATAM.git

3. Levantar Infraestructura:
   docker-compose up -d

4. Configuración Manual de Seguridad (Keycloak):
    - Entrar a http://localhost:8080 (admin/admin).
    - Crear Realm: 'ecoride'.
    - Crear Cliente: 'eco-gateway-client' (Confidential, Service Accounts Enabled).
    - Copiar Secret y ponerlo en 'config-data/gateway.yml'.
    - Crear Rol: 'ROLE_DRIVER'.
    - Crear Usuario: 'driver1'.

5. Ejecutar Servicios (Orden estricto):
    1. Config Server (8888)
    2. Eureka Server (8761)
    3. Trip Service (8081)
    4. API Gateway (8090)

---

## Pruebas de Endpoints

### Crear Viaje (Vía Gateway)
URL: http://localhost:8090/api/trips
Método: POST
Auth: Requiere Login en Keycloak (driver1 / 1234)

Body (JSON):
{
"origin": "Centro",
"destination": "Norte",
"departureTime": "2025-12-01T08:00:00",
"seatsAvailable": 4,
"price": 4500.00
}

---

## Progreso

[x] Fase 1: Infraestructura (Docker, Config, Eureka).
[x] Fase 2: Seguridad Base (Keycloak, Gateway).
[x] Fase 3: Persistencia (TripService + BD).
[ ] Fase 4: Sagas (Pagos).
[ ] Fase 5: Notificaciones.

Curso: Microservicios