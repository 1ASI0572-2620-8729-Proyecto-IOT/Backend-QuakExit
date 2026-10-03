# TerraGuard QuakExit

Backend Spring Boot para monitoreo y evacuacion sismica.

## Requisitos

- Java 25.0.1
- Maven 3.9.16 o compatible
- MySQL compatible con conexiones SSL (por ejemplo, Aiven for MySQL).

El proyecto usa Java 25 y Spring Boot 3.5.6.



Verifica las versiones:

```powershell
java -version
mvn -version
```

Maven debe mostrar Java 25.0.1.

## Ejecutar la aplicacion

### Variables de entorno

En Render configura estas variables:

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://HOST:PUERTO/defaultdb?sslMode=REQUIRED
DB_USER=avnadmin
DB_PASSWORD=tu-password-de-aiven
JWT_SECRET=secreto-base64-largo-y-aleatorio
ADMIN_EMAIL=admin@tu-dominio.com
ADMIN_PASSWORD=tu-password-del-administrador
ADMIN_FULL_NAME=QuakExit Administrator
CORS_ALLOWED_ORIGINS=https://tu-frontend.netlify.app
FLYWAY_ENABLED=true
```

`DB_URL` debe usar el host, puerto y base de datos que entrega Aiven. No uses `localhost` en Render. `JWT_SECRET` debe ser una cadena Base64 válida porque la aplicación la decodifica al iniciar.

Desde la carpeta raiz del proyecto:

```powershell
mvn spring-boot:run
```

La aplicacion inicia en:

```text
http://localhost:8080
```

Para detenerla, presiona `Ctrl+C` en la terminal donde se esta ejecutando.

## Swagger / OpenAPI

Interfaz Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Especificacion OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

La especificacion OpenAPI debe responder con HTTP 200 cuando la aplicacion este activa.

El endpoint publico de salud es `GET /health` y responde `{"status":"UP"}`. Swagger UI (`/swagger-ui/index.html`) y OpenAPI (`/v3/api-docs`) son accesibles sin token.

## Ejecutar como JAR

Compila el proyecto:

```powershell
mvn clean package
```

Ejecuta el artefacto generado:

```powershell
java -jar target\quakexit-0.0.1-SNAPSHOT.jar
```

## Ejecutar pruebas

```powershell
mvn clean test
```

Para verificar tambien el empaquetado:

```powershell
mvn clean verify -Djacoco.skip=false
```


