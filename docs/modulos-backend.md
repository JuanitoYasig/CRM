# Modulos Del Backend

## Modulo Citizen

Ruta base:

```text
/api/v1/contribuyentes
```

Finalidad:

Gestionar el padron de ciudadanos o contribuyentes de cada tenant municipal.

### Entidades y objetos principales

`Contribuyente`
: Entidad principal del modulo. Guarda tipo de identificacion, numero de documento, nombres, apellidos, email, telefono, direccion, estado activo y datos multi tenant heredados de `BaseEntity`.

`Direccion`
: Value object embebido con calle principal, secundaria, numero de predio, referencia y parroquia.

`TipoIdentificacion`
: Enum con `CEDULA`, `RUC_NATURAL`, `RUC_PRIVADA`, `RUC_PUBLICA` y `PASAPORTE`.

### Casos de uso

`RegistrarContribuyenteAction`
: Normaliza el documento, valida duplicados por tenant, crea el contribuyente y lo guarda.

`ContribuyenteQueryService`
: Consulta contribuyentes por id, documento o lista completa del tenant actual.

### Reglas relevantes

- El documento se normaliza con `DataSanitizer.normalizeDocument`.
- La identificacion ecuatoriana se valida en el constructor de `Contribuyente`.
- Existe restriccion unica por `tenant_id` y `numero_identificacion`.
- Todas las consultas se filtran por `tenantId`.

## Modulo Ticket

Ruta base:

```text
/api/v1/tramites
```

Finalidad:

Gestionar solicitudes, requerimientos y tramites ciudadanos dentro de cada jurisdiccion municipal.

### Entidades y objetos principales

`Tramite`
: Entidad principal. Guarda codigo, asunto, descripcion, departamento destino, contribuyente asociado, estado, prioridad, canal, SLA, resolucion y fechas.

`EstadoTramite`
: Estados: `CREADO`, `EN_REVISION`, `DERIVADO`, `RESUELTO`, `CERRADO`.

`PrioridadTramite`
: Prioridades con horas maximas: `BAJA` 120h, `MEDIA` 72h, `ALTA` 24h, `URGENTE` 8h.

`CanalRecepcion`
: Canales: ventanilla presencial, portal web, app movil y linea telefonica.

### Casos de uso

`CrearTramiteAction`
: Verifica que el contribuyente exista en el tenant actual, calcula SLA, genera codigo y crea el tramite.

`DerivarTramiteAction`
: Cambia el departamento destino y marca el tramite como `DERIVADO`.

`ResolverTramiteAction`
: Guarda resolucion, fecha de resolucion y marca el tramite como `RESUELTO`.

`TramiteQueryService`
: Lista, filtra por estado/departamento, busca por id/codigo y genera metricas del dashboard.

### SLA

El SLA se calcula con estrategia:

- `EmergenciaVialSlaStrategy`: para `EMERGENCIA_VIAL` y `AGUA_POTABLE_ROTURA`, vencimiento en 6 horas.
- `StandardSlaStrategy`: usa las horas de la prioridad.
- `SlaCalculatorService`: selecciona la estrategia adecuada.

## Modulo Auth

Ruta base:

```text
/api/v1/auth
```

Finalidad:

Gestionar usuarios institucionales, login, JWT, roles y protecciones de contrasena.

### Entidades y objetos principales

`Usuario`
: Entidad autenticable. Guarda username, hash, salt, email, nombre completo, departamento, rol, estado activo, intentos fallidos y bloqueo temporal.

`Rol`
: Roles: `ADMIN_GENERAL`, `DIRECTOR_DEPARTAMENTAL`, `VENTANILLA_ATENCION`, `TECNICO_OPERATIVO`, `AUDITOR_INTERNO`.

`UserPrincipal`
: Identidad autenticada extraida del JWT y almacenada en `UserContext`.

### Casos de uso y servicios

`RegistrarUsuarioAction`
: Valida username, contrasena minima, duplicados por tenant, genera hash y crea usuario.

`AutenticarUsuarioAction`
: Busca usuario por tenant, valida estado/bloqueo, verifica contrasena, registra fallos y genera JWT.

`PasswordCryptoService`
: Genera hash PBKDF2 con salt aleatorio.

`JwtTokenService`
: Genera y valida JWT firmado con HMAC-SHA256.

`UsuarioQueryService`
: Lista o busca usuarios del tenant actual.

`AdminDataInitializer`
: Crea usuario `admin` para tenants demo.

`DesbloqueoUsuariosJob`
: Ejecuta cada 5 minutos y desbloquea cuentas con bloqueo expirado.

## Modulo Shared

Finalidad:

Contener piezas reutilizables y transversales.

Componentes:

- `BaseEntity`
- `TenantContext`
- `UserContext`
- `WebMvcConfig`
- `GlobalExceptionHandler`
- Excepciones de dominio
- `TenantInterceptor`
- `DataSanitizer`
- `SecureXmlParser`
- `EcuadorDocumentValidator`
