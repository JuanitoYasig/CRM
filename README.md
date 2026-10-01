# API Core CRM Municipal

Proyecto MVP de CRM municipal para gestionar contribuyentes, tramites ciudadanos, autenticacion institucional y monitoreo operativo por tenant. La solucion esta dividida en un backend Java/Spring Boot con arquitectura modular inspirada en DDD y un frontend React/Vite para la operacion diaria.

## Version del proyecto

- Backend: `api-core` version `0.0.1-SNAPSHOT`
- Frontend: `crm-frontend` version `0.0.0`
- Spring Boot: `4.1.1`
- Java: `17`
- React: `19.2.8`
- Vite: `8.3.0`

## Tecnologias principales

- Java 17
- Spring Boot Web MVC
- Spring Data JPA
- MySQL Connector/J
- Maven Wrapper
- React 19
- TypeScript
- Vite
- Tailwind CSS
- TanStack React Query
- Axios
- Lucide React
- Oxlint

## Finalidad

El sistema busca centralizar la atencion municipal mediante dos dominios principales: ciudadanos/contribuyentes y tramites. Cada operacion queda aislada por tenant o jurisdiccion municipal, permitiendo que distintos GAD trabajen sobre la misma aplicacion sin mezclar datos.

La implementacion incluye validaciones ecuatorianas de documentos, sanitizacion de entradas, manejo global de errores, JWT propio con firma HMAC-SHA256, hashing de contrasenas con PBKDF2 y un frontend operativo con login, dashboard, bandeja de tramites y padron de contribuyentes.

## Fases del proyecto

1. Fase 1: Arquitectura DDD, multi tenant, lineamientos OWASP, modulo Ciudadanos y modulo Tramites.
2. Fase 2: Login, roles, JWT, hashing de contrasenas, bloqueo por intentos fallidos y usuarios semilla.
3. Fase 3: Frontend React, integracion con API, validaciones visuales, dashboard, filtros y modales operativos.

La explicacion completa esta en [docs/fases.md](docs/fases.md).

## Comandos de ejecucion

### Requisitos

- Java 17
- MySQL local activo, por ejemplo desde XAMPP
- Node.js compatible con Vite 8
- npm

### Backend

La base configurada por defecto es MySQL:

```bash
./mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

Endpoint rapido de estado:

```bash
curl http://localhost:8080/api/status
```

### Frontend

```bash
cd crm-frontend
npm install
npm run dev
```

El frontend queda disponible normalmente en:

```text
http://localhost:5173
```

### Build y pruebas

Backend:

```bash
./mvnw test
./mvnw clean package
```

Frontend:

```bash
cd crm-frontend
npm run lint
npm run build
```

## Credenciales demo

Al iniciar el backend se crean usuarios administradores para estos tenants:

- `gad-central`
- `gad-milagro`
- `gad-loja`
- `gad-cuenca`

Credenciales:

```text
usuario: admin
password: AdminGAD2026!
```

## Documentacion

- [Arquitectura](docs/arquitectura.md)
- [Fases de implementacion](docs/fases.md)
- [Modulos del backend](docs/modulos-backend.md)
- [API REST](docs/api-rest.md)
- [Seguridad, OWASP y multi tenant](docs/seguridad.md)
- [Frontend React](docs/frontend.md)
- [Ejecucion y configuracion](docs/ejecucion.md)
- [Modelo de datos](docs/modelo-datos.md)

## Estado actual

El backend y frontend tienen estructura funcional para desarrollo local. Los archivos `docker-compose.yml` y `k8s/*.yaml` existen pero actualmente estan vacios, por lo que Docker/Kubernetes quedan como trabajo pendiente antes de usarlos en despliegue.
