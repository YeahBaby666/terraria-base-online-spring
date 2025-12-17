# Dockerfile para el Servidor Spring Boot (Java / Maven)

# 1. ETAPA DE CONSTRUCCIÓN (BUILD STAGE)
# Usa una imagen de Maven para construir el proyecto (compilar y generar el JAR)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de configuración de Maven (pom.xml)
COPY pom.xml .

# Copia todo el código fuente
COPY src ./src

# Construye el proyecto y genera el JAR final
# La bandera -DskipTests es opcional, pero acelera la construcción si no necesitas ejecutar tests de unidad en el contenedor
RUN mvn clean install -DskipTests

# 2. ETAPA DE EJECUCIÓN (RUNTIME STAGE)
# Usa una imagen más pequeña de Java para la ejecución final, lo cual reduce el tamaño del contenedor
FROM eclipse-temurin:21-jre-alpine

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR ejecutable desde la etapa de construcción al contenedor final
# Nota: Ajusta 'tu-app.jar' si tu pom.xml usa un nombre diferente.
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot (8080)
EXPOSE 8080

# Comando para iniciar la aplicación Spring Boot
# Render sobrescribirá el puerto 8080 con $PORT
ENTRYPOINT ["java", "-jar", "app.jar"]