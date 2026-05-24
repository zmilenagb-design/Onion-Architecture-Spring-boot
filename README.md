# API REST - Onion Architecture
## Java · Spring Boot · JPA/Hibernate · PostgreSQL

API RESTful construida con Java y Spring Boot siguiendo el patrón de Onion Architecture, usando JPA/Hibernate con EntityManager para la persistencia de datos y PostgreSQL como base de datos.

## Tecnologías utilizadas
- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven

## Requisitos previos
- Java 17 o superior instalado
- PostgreSQL instalado y corriendo en el puerto 5432
- Base de datos `empresas_db` creada

## Configuración
En `src/main/resources/application.properties` ajusta las credenciales de tu base de datos si son diferentes:

spring.datasource.url=jdbc:postgresql://localhost:5432/empresas_db
spring.datasource.username=postgres
spring.datasource.password=admin123

## Cómo ejecutar
1. Clona el repositorio
2. Abre el proyecto en IntelliJ IDEA
3. Ejecuta la clase `EmpresasApiApplication`
4. La API queda disponible en `http://localhost:8080`

## Endpoints principales
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/companias | Listar todas las compañías |
| GET | /api/companias/{id} | Obtener una compañía |
| POST | /api/companias | Crear compañía |
| PUT | /api/companias/{id} | Actualizar compañía |
| DELETE | /api/companias/{id} | Eliminar compañía |
| GET | /api/empleados | Listar todos los empleados |
| GET | /api/empleados/{id} | Obtener un empleado |
| POST | /api/empleados | Crear empleado |
| PUT | /api/empleados/{id} | Actualizar empleado |
| DELETE | /api/empleados/{id} | Eliminar empleado |
| POST | /api/companias/con-empleados | Crear compañía con empleados (transaccional) |