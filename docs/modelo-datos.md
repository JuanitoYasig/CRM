# Modelo De Datos

## Base comun

Las entidades principales heredan de `BaseEntity`.

Campos comunes:

- `id`
- `tenant_id`
- `created_at`
- `updated_at`

`tenant_id` se asigna automaticamente desde `TenantContext` en `@PrePersist` si la entidad no lo trae.

## Tabla `crm_contribuyentes`

Entidad:

```text
Contribuyente
```

Campos principales:

- `tipo_identificacion`
- `numero_identificacion`
- `nombres`
- `apellidos`
- `email`
- `telefono`
- `activo`
- Campos embebidos de `Direccion`:
  - `calle_principal`
  - `calle_secundaria`
  - `numero_predio`
  - `referencia`
  - `parroquia`

Restricciones:

- Unico por `tenant_id` + `numero_identificacion`.

Indices:

- `tenant_id`, `numero_identificacion`.
- `tenant_id`, `email`.

## Tabla `crm_tramites`

Entidad:

```text
Tramite
```

Campos principales:

- `codigo_tramite`
- `asunto`
- `descripcion`
- `departamento_destino`
- `contribuyente_id`
- `estado`
- `prioridad`
- `canal`
- `fecha_vencimiento_sla`
- `fecha_resolucion`
- `resolucion_texto`

Restricciones:

- Unico por `tenant_id` + `codigo_tramite`.

Indices:

- `tenant_id`, `estado`.
- `tenant_id`, `departamento_destino`.
- `tenant_id`, `contribuyente_id`.

## Tabla `crm_usuarios`

Entidad:

```text
Usuario
```

Campos principales:

- `username`
- `password_hash`
- `password_salt`
- `email`
- `nombre_completo`
- `departamento`
- `rol`
- `activo`
- `intentos_fallidos`
- `bloqueado_hasta`

Restricciones:

- Unico por `tenant_id` + `username`.
- Unico por `tenant_id` + `email`.

Indices:

- `tenant_id`, `username`.
- `tenant_id`, `rol`.

## Relaciones logicas

Actualmente `Tramite` guarda `contribuyenteId` como `Long`, no como relacion JPA `@ManyToOne`.

Flujo logico:

```text
Contribuyente 1 ---- N Tramite
```

La existencia del contribuyente se valida en `CrearTramiteAction` antes de crear el tramite.

## Estados de tramite

```text
CREADO -> DERIVADO -> RESUELTO -> CERRADO
```

Tambien existe `EN_REVISION`, aunque en los endpoints actuales no hay una ruta dedicada para activar esa transicion.

## Tenants semilla

El sistema crea administrador demo para:

- `gad-central`
- `gad-milagro`
- `gad-loja`
- `gad-cuenca`
