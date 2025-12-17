# ETAPA 1: Construcción (Maven)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Copiar solo el pom.xml primero para aprovechar la caché de capas de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
FROM openjdk:21-jdk-slim

# Crear un usuario no raíz por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el jar desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Configurar variables de entorno por defecto
ENV PORT=10001
EXPOSE 10001

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]