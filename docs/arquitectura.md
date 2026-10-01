# Arquitectura Del Proyecto

## Vision general

El proyecto esta construido como un CRM municipal multi tenant. Su objetivo es administrar ciudadanos/contribuyentes, tramites y usuarios institucionales por jurisdiccion municipal, evitando que los datos de un GAD se mezclen con los de otro.

La arquitectura del backend combina:

- Arquitectura modular por dominio.
- Principios DDD para separar modelo, casos de uso, repositorios y adaptadores.
- Multi tenant mediante `TenantContext`, cabecera `X-Tenant-ID` y filtros de repositorio por `tenantId`.
- Practicas OWASP aplicadas en autenticacion, sanitizacion, manejo de errores y procesamiento XML seguro.

## Estructura principal

```text
src/main/java/com/facturacion/api_core
├── ApiCoreApplication.java
├── TestController.java
├── modules
│   ├── auth
│   ├── citizen
│   └── ticket
└── shared
    ├── config
    ├── context
    ├── domain
    ├── exception
    ├── security
    └── util
```

## Capas por modulo

Cada modulo de negocio sigue una separacion similar:

```text
modules/<modulo>
├── domain
│   ├── model
│   └── repository
├── actions
├── services
├── dto
└── infrastructure
    ├── persistence
    └── web
```

## Responsabilidad de cada capa

`domain/model`
: Contiene entidades, value objects, enums y reglas de negocio. Ejemplos: `Contribuyente`, `Tramite`, `Usuario`, `Direccion`, `Rol`.

`domain/repository`
: Define puertos de repositorio sin depender directamente de Spring Data. Ejemplos: `ContribuyenteRepository`, `TramiteRepository`, `UsuarioRepository`.

`actions`
: Implementa casos de uso transaccionales. Ejemplos: registrar contribuyente, crear tramite, derivar tramite, resolver tramite, registrar usuario e iniciar sesion.

`services`
: Contiene consultas o servicios de dominio/aplicacion. Ejemplos: `TramiteQueryService`, `SlaCalculatorService`, `JwtTokenService`, `PasswordCryptoService`.

`dto`
: Define los contratos de entrada y salida de la API REST.

`infrastructure/web`
: Expone controladores REST.

`infrastructure/persistence`
: Adapta los puertos del dominio a Spring Data JPA.

## Flujo de una peticion

```text
Cliente React / cURL
  ↓
Controller REST
  ↓
Action o QueryService
  ↓
Repositorio de dominio
  ↓
Adaptador JPA
  ↓
Spring Data Repository
  ↓
Base de datos MySQL
```

Antes de llegar al controlador, los interceptores registrados en `WebMvcConfig` procesan la peticion:

1. `TenantInterceptor` resuelve el tenant desde `X-Tenant-ID` o `tenantId`.
2. `JwtAuthenticationFilter` valida el token Bearer cuando existe y fija el usuario actual.
3. El controlador ejecuta el caso de uso correspondiente.

## Componentes compartidos

`BaseEntity`
: Superclase JPA con `id`, `tenantId`, `createdAt` y `updatedAt`. En `@PrePersist` asigna automaticamente el tenant actual cuando la entidad no lo trae definido.

`TenantContext`
: ThreadLocal que guarda el tenant de la peticion actual. Su valor por defecto es `gad-central`.

`UserContext`
: ThreadLocal que guarda el usuario autenticado extraido del JWT.

`GlobalExceptionHandler`
: Estandariza errores HTTP y evita exponer stack traces o detalles internos.

`DataSanitizer`
: Limpia scripts, etiquetas HTML y caracteres de control; tambien normaliza documentos.

`EcuadorDocumentValidator`
: Valida cedula, RUC natural, RUC privado, RUC publico y pasaporte.

`SecureXmlParser`
: Configura parsing XML defensivo contra XXE.

## Decision arquitectonica importante

El proyecto no usa Spring Security como dependencia. La autenticacion y el contexto de usuario estan implementados con interceptores MVC propios:

- `TenantInterceptor`
- `JwtAuthenticationFilter`

Esto hace que el flujo sea mas transparente para fines educativos, pero en una evolucion productiva podria migrarse a Spring Security para RBAC declarativo, filtros estandar y protecciones adicionales.
