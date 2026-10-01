# TerraGuard QuakExit

Backend Spring Boot para monitoreo y evacuacion sismica.

## Requisitos

- Java 25.0.1
- Maven 3.9.16 o compatible
- MySQL ejecutandose en `localhost:3306`

El proyecto usa Java 25 y Spring Boot 3.5.6.



Verifica las versiones:

```powershell
java -version
mvn -version
```

Maven debe mostrar Java 25.0.1.

## Ejecutar la aplicacion

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


