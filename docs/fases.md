# Fases De Implementacion

## Fase 1: DDD, multi tenant, OWASP, Ciudadanos y Tramites

La primera fase construyo la base del backend y los dos modulos principales del negocio municipal.

### Objetivos

- Definir una arquitectura modular cercana a DDD.
- Separar dominio, casos de uso, DTOs, controladores e infraestructura.
- Agregar aislamiento multi tenant.
- Aplicar controles iniciales de seguridad inspirados en OWASP.
- Implementar los modulos Ciudadanos y Tramites.

### Elementos implementados

- `BaseEntity` con `tenantId`, auditoria de creacion y actualizacion.
- `TenantContext` para guardar el tenant actual por request.
- `TenantInterceptor` para resolver `X-Tenant-ID`.
- `GlobalExceptionHandler` para respuestas de error seguras.
- `DataSanitizer` para reducir riesgos de XSS/inyeccion de HTML.
- `SecureXmlParser` para mitigar XXE.
- `EcuadorDocumentValidator` para validar documentos ecuatorianos.
- Modulo `citizen` con registro y consulta de contribuyentes.
- Modulo `ticket` con creacion, listado, derivacion, resolucion, SLA y metricas.

### Finalidad

Esta fase deja armado el nucleo del sistema: cada municipio o GAD puede registrar contribuyentes y tramites sin compartir datos con otros tenants. Tambien define el estilo del codigo para que nuevos modulos sigan el mismo patron.

## Fase 2: Login, roles, JWT y hashing

La segunda fase agrego autenticacion institucional y protecciones de acceso.

### Objetivos

- Registrar usuarios/funcionarios municipales.
- Autenticar con usuario, contrasena y tenant.
- Generar JWT firmado.
- Manejar roles institucionales.
- Guardar contrasenas de forma segura.
- Bloquear cuentas ante fuerza bruta.

### Elementos implementados

- Modulo `auth`.
- Entidad `Usuario`.
- Enum `Rol`.
- `PasswordCryptoService` con PBKDF2 HMAC-SHA-512, salt aleatorio y 210000 iteraciones.
- `JwtTokenService` con tokens JWT HS256.
- `AutenticarUsuarioAction`.
- `RegistrarUsuarioAction`.
- `JwtAuthenticationFilter`.
- `UserContext`.
- `AdminDataInitializer` para crear usuario `admin` en tenants demo.
- `DesbloqueoUsuariosJob` para desbloquear cuentas cuyo castigo expiro.

### Finalidad

Esta fase permite que los funcionarios entren al sistema con una cuenta institucional y que el frontend consuma la API con un token Bearer. Tambien evita que un usuario de un tenant opere accidentalmente sobre datos de otro tenant.

## Fase 3: Frontend React y validaciones

La tercera fase implemento la interfaz de usuario.

### Objetivos

- Crear un frontend operativo con React y Vite.
- Integrar login con el backend.
- Persistir token, usuario y tenant en `localStorage`.
- Consumir endpoints de contribuyentes, tramites y dashboard.
- Agregar filtros, busquedas, badges, modales y validaciones de formulario.

### Elementos implementados

- `crm-frontend` con React, TypeScript y Vite.
- `AuthContext` para sesion y tenant.
- Cliente Axios centralizado en `src/api/client.ts`.
- `LoginView`.
- `DashboardView`.
- `CitizenListView`.
- `TicketListView`.
- Modales de creacion, derivacion y resolucion.
- React Query para cache, carga y refresco automatico.

### Finalidad

Esta fase convierte la API en una aplicacion usable. El funcionario puede iniciar sesion, visualizar metricas, registrar contribuyentes, crear tramites, derivarlos y resolverlos desde una interfaz web.

## Resumen de evolucion

```text
Fase 1: Nucleo backend y dominios principales
Fase 2: Seguridad institucional y autenticacion
Fase 3: Experiencia de usuario con React
```

La secuencia tiene sentido: primero se definio el dominio y la arquitectura, luego se protegieron los accesos y finalmente se construyo la interfaz de operacion.
