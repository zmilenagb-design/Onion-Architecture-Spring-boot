# API REST - Onion Architecture
## Java · Spring Boot · JPA/Hibernate · PostgreSQL

API RESTful construida con Java y Spring Boot siguiendo el patrón de Onion Architecture, usando JPA/Hibernate con EntityManager para la persistencia de datos y PostgreSQL como base de datos.

---

## Tecnologías utilizadas
- Java 21
- Spring Boot 4.0.6
- Spring Data JPA / Hibernate
- Spring Security 7
- PostgreSQL 16
- Maven
- JWT (jjwt 0.12.6)
- Springdoc OpenAPI (Swagger)

## Requisitos previos
- Java 21 instalado
- PostgreSQL instalado y corriendo en el puerto 5432
- Base de datos `empresas_db` creada

## Configuración
En `src/main/resources/application.properties` ajusta las credenciales de tu base de datos si son diferentes:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/empresas_db
spring.datasource.username=postgres
spring.datasource.password=admin123
```

## Variables de entorno
Las siguientes variables se configuran en `application.properties`:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `app.jwt.secret` | Clave secreta para firmar el JWT (mínimo 256 bits) | `mi-clave-secreta-...` |
| `app.jwt.expiration` | Tiempo de expiración del token en milisegundos | `3600000` (1 hora) |
| `spring.datasource.url` | URL de conexión a PostgreSQL | `jdbc:postgresql://localhost:5432/empresas_db` |

## Cómo ejecutar
1. Clona el repositorio
2. Abre el proyecto en IntelliJ IDEA
3. Ejecuta la clase `EmpresasApiApplication`
4. La API queda disponible en `http://localhost:8080`
5. Swagger UI disponible en `http://localhost:8080/swagger-ui/index.html`

## Endpoints principales
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | /api/auth/registro | Registrar nuevo usuario | Público |
| POST | /api/auth/login | Iniciar sesión y obtener token | Público |
| GET | /api/companias | Listar todas las compañías | TOKEN |
| GET | /api/companias/{id} | Obtener una compañía | TOKEN |
| POST | /api/companias | Crear compañía | TOKEN |
| PUT | /api/companias/{id} | Actualizar compañía | TOKEN |
| DELETE | /api/companias/{id} | Eliminar compañía | ADMIN |
| GET | /api/empleados | Listar todos los empleados | TOKEN |
| GET | /api/empleados/{id} | Obtener un empleado | TOKEN |
| POST | /api/empleados | Crear empleado | TOKEN + LimiteSalario |
| PUT | /api/empleados/{id} | Actualizar empleado | TOKEN + Ownership |
| PATCH | /api/empleados/{id} | Actualizar parcialmente | TOKEN + Ownership |
| DELETE | /api/empleados/{id} | Eliminar empleado | TOKEN + Ownership |
| POST | /api/companias/con-empleados | Crear compañía con empleados | TOKEN |

---

## Seguridad

### Autenticación con JWT
El sistema usa JSON Web Tokens (JWT) para autenticación stateless. Para acceder a los endpoints protegidos, el cliente debe:

1. Registrarse en `POST /api/auth/registro` o hacer login en `POST /api/auth/login`
2. Incluir el token en el header de cada petición:

```
Authorization: Bearer <token>
```

El token expira según la configuración de `app.jwt.expiration`.

### Autorización por roles
Existen dos roles en el sistema:

| Rol | Permisos |
|---|---|
| `USER` | Consultar, crear y editar empleados de su propia compañía |
| `ADMIN` | Acceso total sin restricciones |

### Autorización por políticas (policies)

#### EsPropietarioDeCompania
Un usuario con rol `USER` solo puede actualizar o eliminar empleados cuya `companiaId` coincida con la `companiaId` almacenada en su token JWT.

**Regla:**
```
SI usuario.rol == ADMIN → PERMITIR
SI empleado.companiaId == token.companiaId → PERMITIR
EN CUALQUIER OTRO CASO → DENEGAR (403)
```

Implementada con SpEL en `@PreAuthorize`:
```java
@PreAuthorize("@empleadoSecurity.isOwner(#id, authentication)")
```

**Ejemplo — Dueño edita su empleado (`200 OK`):**
```
PUT /api/empleados/7
Authorization: Bearer <token_user_innova2 (companiaId: 2)>

Respuesta: 200 OK
```

**Ejemplo — Usuario de otra compañía intenta editar (`403 Forbidden`):**
```json
{
    "status": 403,
    "error": "Acceso denegado",
    "mensaje": "No tienes permisos para realizar esta acción",
    "ruta": "/api/empleados/7"
}
```

#### LimiteSalario
Un usuario no puede crear o editar empleados con un salario superior al valor del claim `limiteSalario` de su token.

**Regla:**
```
SI usuario.rol == ADMIN → PERMITIR
SI empleadoDTO.salario <= token.limiteSalario → PERMITIR
EN CUALQUIER OTRO CASO → DENEGAR (403)
```

**Ejemplo — Usuario supera su límite (`403 Forbidden`):**
```
POST /api/empleados
Authorization: Bearer <token_user_digital2 (limiteSalario: 3000000)>
Body: { "salario": 5000000 }
```

```json
{
    "status": 403,
    "error": "Acceso denegado",
    "mensaje": "No tienes permisos para realizar esta acción",
    "ruta": "/api/empleados"
}
```

### Por qué ADMIN puede saltarse las reglas de ownership
El rol `ADMIN` representa un superusuario del sistema con acceso total. En el `EmpleadoSecurityService`, el bypass de ADMIN se evalúa **antes** de verificar el ownership:

```java
if (esAdmin) {
    return true; // Acceso inmediato sin verificar companiaId
}
```

Esto permite que los administradores gestionen empleados de cualquier compañía sin restricciones.

### Orden de evaluación 404 vs 403
Para no revelar la existencia de recursos a posibles atacantes, el sistema verifica primero si el empleado existe antes de evaluar el ownership:

```
1. ¿Existe el empleado? → NO  → 404 Not Found
2. ¿Es ADMIN?           → SÍ  → 200 OK
3. ¿companiaId coincide?→ SÍ  → 200 OK
                         → NO  → 403 Forbidden
```

---

## Comparación con ASP.NET Core

| Concepto | ASP.NET Core | Spring Boot (este proyecto) |
|---|---|---|
| Registro de política | `services.AddAuthorization(o => o.AddPolicy(...))` | `@EnableMethodSecurity` + `@Component` |
| Aplicación en endpoint | `[Authorize(Policy = "EsPropietario")]` | `@PreAuthorize("@empleadoSecurity.isOwner(...)")` |
| Handler/Evaluador | `AuthorizationHandler<TRequirement>` | `EmpleadoSecurityService` (`@Component`) |
| Respuesta denegada | Middleware de autorización | `CustomAccessDeniedHandler` |
| Autenticación JWT | `AddAuthentication().AddJwtBearer()` | `JwtAuthenticationFilter` + `SecurityConfig` |
| Roles | `[Authorize(Roles="Admin")]` | `@PreAuthorize("hasRole('ADMIN')")` |