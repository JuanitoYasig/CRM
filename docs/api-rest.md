# API REST

## Base URL

```text
http://localhost:8080/api/v1
```

## Headers importantes

```http
X-Tenant-ID: gad-central
Authorization: Bearer <token>
Content-Type: application/json
```

`X-Tenant-ID` define la jurisdiccion municipal. Si no se envia, el sistema usa `gad-central`.

## Estado

### GET `/api/status`

Endpoint simple para verificar que el backend responde.

Respuesta aproximada:

```json
{
  "mensaje": "¡Hola desde Spring Boot!",
  "estado": "Online",
  "base_datos": "Conectada a XAMPP"
}
```

## Auth

### POST `/api/v1/auth/login`

Inicia sesion y devuelve un JWT.

Body:

```json
{
  "username": "admin",
  "password": "AdminGAD2026!",
  "tenantId": "gad-central"
}
```

Respuesta:

```json
{
  "token": "<jwt>",
  "tokenType": "Bearer",
  "username": "admin",
  "nombreCompleto": "Administrador General del GAD",
  "departamento": "ALCALDIA_DIRECCION_GENERAL",
  "rol": "ADMIN_GENERAL",
  "rolDescripcion": "Administrador General del Sistema",
  "tenantId": "gad-central",
  "expiraEnSegundos": 28800
}
```

### POST `/api/v1/auth/register`

Crea un usuario institucional en el tenant actual.

Body:

```json
{
  "username": "jperez",
  "password": "ClaveSegura123",
  "email": "jperez@gad.gob.ec",
  "nombreCompleto": "Juan Perez",
  "departamento": "OBRAS_PUBLICAS",
  "rol": "TECNICO_OPERATIVO"
}
```

### GET `/api/v1/auth/me`

Devuelve el usuario autenticado segun el JWT.

### GET `/api/v1/auth/usuarios`

Lista usuarios del tenant actual.

## Contribuyentes

### POST `/api/v1/contribuyentes`

Registra un contribuyente.

Body:

```json
{
  "tipoIdentificacion": "CEDULA",
  "numeroIdentificacion": "0102030405",
  "nombres": "Maria",
  "apellidos": "Lopez",
  "email": "maria@example.com",
  "telefono": "0999999999",
  "direccion": {
    "callePrincipal": "Av. Principal",
    "calleSecundaria": "Calle 2",
    "numeroPredio": "N-12",
    "referencia": "Frente al parque",
    "parroquia": "Centro"
  }
}
```

### GET `/api/v1/contribuyentes`

Lista contribuyentes del tenant actual.

### GET `/api/v1/contribuyentes/{id}`

Busca un contribuyente por id dentro del tenant actual.

### GET `/api/v1/contribuyentes/identificacion/{documento}`

Busca por numero de identificacion normalizado.

## Tramites

### POST `/api/v1/tramites`

Crea un tramite asociado a un contribuyente.

Body:

```json
{
  "contribuyenteId": 1,
  "tipoServicio": "EMERGENCIA_VIAL",
  "asunto": "Bache en via principal",
  "descripcion": "Se solicita revision urgente",
  "departamentoDestino": "OBRAS_PUBLICAS",
  "prioridad": "URGENTE",
  "canal": "VENTANILLA_PRESENCIAL"
}
```

### GET `/api/v1/tramites`

Lista tramites del tenant actual.

Filtros opcionales:

```text
?estado=CREADO
?departamento=OBRAS_PUBLICAS
```

### GET `/api/v1/tramites/{id}`

Busca un tramite por id.

### GET `/api/v1/tramites/codigo/{codigo}`

Busca un tramite por codigo.

### PATCH `/api/v1/tramites/{id}/derivar`

Deriva un tramite a otro departamento.

Body:

```json
{
  "nuevoDepartamento": "AGUA_POTABLE",
  "observacion": "Corresponde a mantenimiento hidrosanitario"
}
```

### PATCH `/api/v1/tramites/{id}/resolver`

Resuelve un tramite.

Body:

```json
{
  "resolucionTexto": "Se atendio el requerimiento y se notifico al ciudadano."
}
```

### GET `/api/v1/tramites/dashboard/metricas`

Devuelve metricas del dashboard:

- Total de tramites.
- Tramites creados.
- En revision.
- Derivados.
- Resueltos.
- Cerrados.
- Vencidos por SLA.
- Distribucion por departamento.

## Formato de errores

Los errores se devuelven con estructura uniforme:

```json
{
  "timestamp": "2026-10-01T00:00:00Z",
  "status": 422,
  "error": "422 UNPROCESSABLE_ENTITY",
  "code": "VALIDATION_ERROR",
  "message": "Mensaje de error",
  "tenantId": "gad-central"
}
```
