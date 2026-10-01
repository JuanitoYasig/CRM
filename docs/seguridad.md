# Seguridad, OWASP Y Multi Tenant

## Multi tenant

El sistema aisla datos por tenant usando:

- Cabecera `X-Tenant-ID`.
- Parametro opcional `tenantId`.
- `TenantContext` basado en `ThreadLocal`.
- Campo `tenant_id` en entidades que heredan de `BaseEntity`.
- Consultas de repositorio filtradas por `tenantId`.
- Restricciones unicas compuestas por tenant.

Ejemplo:

```text
tenant_id + numero_identificacion
tenant_id + codigo_tramite
tenant_id + username
tenant_id + email
```

Esto permite que dos municipios tengan un usuario `admin`, un mismo numero de tramite o un mismo documento sin chocar entre si, siempre que pertenezcan a tenants diferentes.

## Flujo del tenant

1. El frontend envia `X-Tenant-ID`.
2. `TenantInterceptor` lo lee y lo guarda en `TenantContext`.
3. `BaseEntity` asigna ese tenant al persistir nuevas entidades.
4. Servicios y repositorios consultan usando el tenant actual.
5. Al terminar la peticion, el contexto se limpia.

## Autenticacion JWT

El login genera un JWT con:

- `iss`
- `sub`
- `userId`
- `tenantId`
- `departamento`
- `role`
- `iat`
- `exp`

`JwtTokenService` firma el token con HMAC-SHA256 y valida la firma usando comparacion en tiempo constante.

`JwtAuthenticationFilter` lee el header:

```http
Authorization: Bearer <token>
```

Si el token es valido, construye un `UserPrincipal`, fija `UserContext` y ajusta el tenant segun la informacion firmada.

## Roles

Roles disponibles:

- `ADMIN_GENERAL`
- `DIRECTOR_DEPARTAMENTAL`
- `VENTANILLA_ATENCION`
- `TECNICO_OPERATIVO`
- `AUDITOR_INTERNO`

Actualmente los roles viajan en el JWT y se exponen en el contexto de usuario. El proyecto todavia no aplica autorizacion granular por endpoint con anotaciones o reglas centralizadas.

## Hashing de contrasenas

`PasswordCryptoService` usa:

- PBKDF2 con HMAC-SHA-512.
- 210000 iteraciones.
- Salt aleatorio de 16 bytes.
- Hash de 256 bits.
- Comparacion con `MessageDigest.isEqual`.

El sistema nunca devuelve hash ni salt en los DTOs de usuario.

## Defensa contra fuerza bruta

`AutenticarUsuarioAction` aplica:

- Maximo 5 intentos fallidos.
- Bloqueo temporal de 15 minutos.
- Mensajes genericos para evitar enumeracion de usuarios.
- Reinicio de intentos al autenticar correctamente.

`DesbloqueoUsuariosJob` revisa cada 5 minutos cuentas cuyo bloqueo expiro.

## Sanitizacion

`DataSanitizer`:

- Elimina etiquetas `<script>`.
- Elimina tags HTML.
- Quita caracteres de control.
- Escapa caracteres HTML sensibles.
- Normaliza documentos quitando simbolos y pasando a mayusculas.

Se usa en entidades como `Contribuyente`, `Tramite` y `Usuario`.

## Validacion de documentos ecuatorianos

`EcuadorDocumentValidator` valida:

- Cedula.
- RUC persona natural.
- RUC sociedad privada.
- RUC institucion publica.
- Pasaporte por longitud.

La validacion ocurre al crear un `Contribuyente`.

## Manejo seguro de errores

`GlobalExceptionHandler` evita exponer detalles internos. Para errores no controlados genera un `incidentId` y devuelve un mensaje generico.

Esto ayuda frente a OWASP A05, porque no se filtran stack traces ni mensajes tecnicos sensibles al cliente.

## XML seguro

`SecureXmlParser` deshabilita:

- DOCTYPE.
- Entidades externas generales.
- Entidades externas de parametro.
- Carga externa de DTD.
- XInclude.
- Expansion de entidades.

Esto mitiga XXE cuando en el futuro se procesen documentos XML.

## Limitaciones actuales

- No existe dependencia de Spring Security.
- No hay permisos por rol aplicados de forma declarativa en endpoints.
- La clave JWT por defecto esta en configuracion del codigo mediante `@Value`; en produccion debe venir de variable de entorno o secreto externo.
- `docker-compose.yml` y manifiestos `k8s` estan vacios.
- No hay migraciones versionadas tipo Flyway/Liquibase; se usa `hibernate.ddl-auto=update`.
