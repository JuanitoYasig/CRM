# Frontend React

## Ubicacion

```text
crm-frontend/
```

## Finalidad

El frontend es la interfaz operativa para funcionarios municipales. Permite iniciar sesion, seleccionar tenant, ver metricas, administrar contribuyentes y gestionar tramites.

## Tecnologias

- React `19.2.8`
- TypeScript `~6.0.2`
- Vite `8.3.0`
- Tailwind CSS `4.3.3`
- TanStack React Query `5.104.0`
- Axios `1.20.0`
- Lucide React `1.49.0`
- Oxlint `1.81.0`

## Estructura

```text
crm-frontend/src
├── api
│   ├── authApi.ts
│   ├── citizenApi.ts
│   ├── client.ts
│   └── ticketApi.ts
├── components
│   ├── auth
│   ├── citizens
│   ├── common
│   ├── dashboard
│   ├── layout
│   └── tickets
├── context
│   └── AuthContext.tsx
├── types
│   └── index.ts
├── App.tsx
└── CRMApp.tsx
```

## Cliente API

`src/api/client.ts` configura Axios con:

- `baseURL: /api/v1`
- `Content-Type: application/json`
- Header `Authorization` si existe token.
- Header `X-Tenant-ID`.
- Limpieza de sesion ante error `401`.

Como la base URL es relativa, durante desarrollo Vite debe reenviar `/api` al backend o se debe configurar proxy si todavia no existe.

## Autenticacion en frontend

`AuthContext` maneja:

- `token`
- `user`
- `tenantId`
- `login`
- `logout`
- `setTenantId`
- `isAuthenticated`

Los datos se guardan en `localStorage`:

- `gad_crm_token`
- `gad_crm_user`
- `gad_crm_tenant`

## Pantallas principales

`LoginView`
: Formulario de ingreso con usuario, contrasena y tenant. Viene precargado para demo con `admin` y `AdminGAD2026!`.

`DashboardView`
: Consulta metricas de tramites y muestra KPIs: total, en gestion, resueltos/cerrados y vencidos fuera de SLA. Refresca cada 15 segundos.

`CitizenListView`
: Lista contribuyentes, permite busqueda, filtro por tipo de documento, registro de contribuyentes y creacion de tramite desde un ciudadano.

`TicketListView`
: Lista tramites, permite busqueda, filtro por estado, departamento y vencimiento SLA. Tambien abre modales para crear, derivar, resolver y ver detalle.

## Integracion con backend

Servicios:

- `authApi`: login y perfil.
- `citizenApi`: listar, buscar y crear contribuyentes.
- `ticketApi`: listar, buscar, crear, derivar, resolver y obtener metricas.

## Validaciones y experiencia de usuario

El frontend incluye:

- Campos requeridos en formularios.
- Mensajes de error de autenticacion.
- Estados de carga.
- Estados de error de conexion.
- Filtros client-side.
- Badges visuales de estado y prioridad.
- Refresco automatico de metricas y tramites.

## Comandos

```bash
cd crm-frontend
npm install
npm run dev
npm run lint
npm run build
npm run preview
```
