# Backend Paolicias.Sweet

Backend de la app Android **Paolicias.Sweet**, formado por 3 microservicios Spring Boot. Cada uno tiene su propia base de datos PostgreSQL y corre en Docker. Se despliega en una instancia EC2 de AWS.

## Microservicios

| Microservicio | Puerto API | Puerto BD (local) | Base de datos | Responsabilidad |

| `usuarios` | 8083 | 5433 | `usuariosdb` | Gestión de usuarios (clientes y administradores) |

| `productos` | 8081 | 5434 | `productosdb` | Catálogo de productos y sus fotos |

| `reservas` | 8082 | 5435 | `reservasdb` | Reservas de los clientes y comprobantes de pago |

- **usuarios:** crea, consulta, actualiza y elimina usuarios; permite buscar por email.
- **productos:** CRUD del catálogo; guarda la foto de cada producto en la base de datos y la entrega por `GET /api/productos/{id}/imagen`.
- **reservas:** crea reservas, las filtra por cliente (`?email=`) o estado (`?estado=`), cambia su estado y guarda el comprobante de pago.

Los microservicios **no se llaman entre sí**: cada uno tiene su base de datos y la app Android coordina las consultas. Las reservas identifican al cliente por su `clienteEmail`.

## Arquitectura


App Android ──► usuarios (8083)  ──► PostgreSQL usuariosdb
            ├─► productos (8081) ──► PostgreSQL productosdb
            └─► reservas (8082)  ──► PostgreSQL reservasdb


## Ejecución con Docker

Cada carpeta tiene su `Dockerfile` y su `docker-compose.yml` (API + PostgreSQL).

1. Crea un archivo `.env` dentro de la carpeta del microservicio (no se sube al repositorio), con tus propios valores:
   DB_USER=...
   DB_PASSWORD=...

2. Levanta el servicio en productos: docker compose up -d --build
   
3. Repite para usuarios y reservas.

Para detener un servicio: docker compose down

## Puertos a abrir en AWS (grupo de seguridad)

| Puerto | Uso |
|---|---|
| 22 | SSH |
| 8081 | API productos |
| 8082 | API reservas |
| 8083 | API usuarios |

Los puertos de las bases de datos (5433–5435) son solo para desarrollo local y no se abren en AWS.

## Tecnologías

Spring Boot 3 · Java 21 · Spring Data JPA · PostgreSQL 16 · Docker / Docker Compose · AWS EC2
