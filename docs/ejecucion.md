# Ejecucion Y Configuracion

## Requisitos

- Java 17.
- Maven Wrapper incluido en el proyecto.
- MySQL local.
- Node.js y npm para el frontend.

## Configuracion backend

Archivo:

```text
src/main/resources/application.yml
```

Configuracion actual:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_spring_boot?createDatabaseIfNotExist=true
    username: root
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
server:
  port: 8080
```

La base de datos se crea automaticamente si MySQL lo permite. Hibernate actualiza las tablas con `ddl-auto: update`.

## Ejecutar backend

Desde la raiz del proyecto:

```bash
./mvnw spring-boot:run
```

API:

```text
http://localhost:8080
```

Estado:

```bash
curl http://localhost:8080/api/status
```

## Ejecutar frontend

```bash
cd crm-frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

## Credenciales demo

El backend crea usuario `admin` con contrasena `AdminGAD2026!` en:

- `gad-central`
- `gad-milagro`
- `gad-loja`
- `gad-cuenca`

## Pruebas backend

```bash
./mvnw test
```

Pruebas detectadas:

- `ApiCoreApplicationTests`
- `EcuadorDocumentValidatorTest`
- `JwtTokenServiceTest`
- `PasswordCryptoServiceTest`
- `SecureXmlParserTest`

## Build backend

```bash
./mvnw clean package
```

El artefacto se genera bajo:

```text
target/
```

## Lint y build frontend

```bash
cd crm-frontend
npm run lint
npm run build
```

## Variables y secretos recomendados

Aunque el proyecto trae valores por defecto, para produccion conviene externalizar:

- URL de base de datos.
- Usuario y contrasena de base de datos.
- Secreto JWT.
- Duracion de token.

Ejemplo conceptual:

```bash
export JWT_SECRET="valor-largo-y-seguro"
export JWT_EXPIRATION_SECONDS="28800"
```

## Docker y Kubernetes

Actualmente:

- `docker-compose.yml` existe pero esta vacio.
- `k8s/namespace.yaml` existe pero esta vacio.
- `k8s/api-core.yaml` existe pero esta vacio.
- `k8s/postgres.yaml` existe pero esta vacio.

Por eso no hay comandos Docker/Kubernetes operativos todavia. Antes de desplegar por contenedores se debe completar:

- Dockerfile del backend.
- Dockerfile o build estatico del frontend.
- `docker-compose.yml` con MySQL/PostgreSQL y servicios.
- Manifiestos Kubernetes con namespace, deployment, service, configmap y secrets.
