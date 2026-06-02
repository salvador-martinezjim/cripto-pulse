# 📘 APRENDIZAJES — Cripto Pulse

> Documentación técnica de competencias adquiridas durante el desarrollo del proyecto.

---

## 1. Introducción al Proyecto

**Cripto Pulse** es una API REST desarrollada con **Java 21** y **Spring Boot 4**, diseñada para resolver un problema concreto: la dificultad de monitorear el mercado de criptomonedas de forma sencilla, persistente y automatizada.

La mayoría de las plataformas de monitoreo de criptomonedas son complejas, costosas o no ofrecen control sobre los datos históricos del usuario. Cripto Pulse ataca este problema al proveer una API de backend que:

- Consulta **precios en tiempo real** directamente desde la API pública de CoinGecko.
- **Persiste el historial** de precios en una base de datos relacional, habilitando el análisis de tendencias.
- Permite al usuario **configurar alertas personalizadas** (por encima o por debajo de un precio objetivo), disparadas automáticamente por un proceso en segundo plano.
- Se despliega de forma aislada y reproducible mediante **contenedores Docker**, eliminando el clásico problema de "en mi máquina sí funciona".

El proyecto fue concebido como un ejercicio de ingeniería real de punta a punta, aplicando desde el diseño del modelo de datos hasta la automatización del despliegue.

---

## 2. Arquitectura y Tecnologías

### 2.1 Stack Tecnológico Principal

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 (LTS) |
| Framework | Spring Boot | 4.0.6 |
| Persistencia | Spring Data JPA + Hibernate | — |
| Motor de BD | PostgreSQL | 16.13 |
| Migraciones de BD | Flyway | (integrado con Spring) |
| Reducción de boilerplate | Lombok | — |
| Integración externa | CoinGecko API (REST) | v3 |
| Contenerización | Docker + Docker Compose | — |
| Gestor de dependencias | Apache Maven | 3.9.6 |

### 2.2 Arquitectura en Capas (N-Tier)

El proyecto sigue una **arquitectura orientada a servicios en capas**, un patrón estándar en aplicaciones empresariales Spring que garantiza separación de responsabilidades y facilita el mantenimiento y las pruebas:

```
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                  │
│   PriceController  │  AlertController  │  HistoryController  │  CryptoCatalogController
│              (REST API — JSON sobre HTTP)                │
└──────────────────────────┬──────────────────────────────┘
                           │ invoca
┌──────────────────────────▼──────────────────────────────┐
│                    CAPA DE SERVICIOS                     │
│   CryptoService   │   AlertService   │  AlertMonitor (Scheduler)   │  HistoryService
│        (Lógica de negocio, integración con CoinGecko)   │
└──────────────────────────┬──────────────────────────────┘
                           │ accede
┌──────────────────────────▼──────────────────────────────┐
│                  CAPA DE ACCESO A DATOS                  │
│  AlertRepository │ PriceHistoryRepository │ SupportedCryptoRepository
│          (Spring Data JPA — interfaz con PostgreSQL)     │
└──────────────────────────┬──────────────────────────────┘
                           │ persiste en
┌──────────────────────────▼──────────────────────────────┐
│                   BASE DE DATOS                          │
│                    PostgreSQL 16                         │
│   users │ cryptocurrencies │ price_history │ alerts │ supported_cryptos
└─────────────────────────────────────────────────────────┘
```

### 2.3 Componentes Transversales Destacados

**`AlertMonitor` — Cron Job Automático:**
Implementado como un `@Component` con `@Scheduled(fixedRate = 30000)`, este componente se ejecuta cada 30 segundos de forma independiente al ciclo de peticiones HTTP. Consulta todas las alertas con estado distinto de `TRIGGERED` en la base de datos, obtiene los precios actuales de CoinGecko para las monedas involucradas y actualiza el estado a `TRIGGERED` si la condición (`ABOVE` / `BELOW`) se cumple. Habilitar este mecanismo requirió la anotación `@EnableScheduling` en la clase principal.

**`@EnableCaching` y `@Cacheable`:**
El método `getMarketHistory()` en `CryptoService` utiliza `@Cacheable(value = "history", key = "#symbol")` para almacenar en memoria el historial de precios de los últimos 7 días por moneda, evitando llamadas repetidas a la API externa de CoinGecko y mejorando el tiempo de respuesta para el cliente React.

**`CorsConfig` — Configuración de CORS:**
Una clase de configuración `@Configuration` que implementa `WebMvcConfigurer` y abre el acceso cross-origin para todos los endpoints (`/**`), permitiendo que cualquier frontend (como una app React) se conecte durante el desarrollo.

**Flyway — Versionado de Base de Datos:**
El esquema relacional no se crea manualmente: se gestiona mediante scripts SQL versionados (`V1` a `V5`) que Flyway ejecuta en orden al iniciar la aplicación. Esto garantiza trazabilidad y reproducibilidad del estado de la base de datos en cualquier entorno.

```
V1__Init_Tables.sql          → Crea: users, cryptocurrencies, price_history, alerts
V2__Insert_Base_Cryptos.sql  → Datos semilla: BTC, ETH, etc. en 'cryptocurrencies'
V3__Insert_Default_User.sql  → Usuario de prueba por defecto (UUID fijo)
V4__Create_Supported_Cryptos.sql → Nueva tabla 'supported_cryptos' con catálogo ampliado
V5__Link_Alerts_To_Catalog.sql   → Migra la FK de 'alerts' hacia la nueva tabla
```

### 2.4 Mapa de Endpoints REST

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/v1/prices` | Precios actuales (parámetro `?symbols=bitcoin,ethereum`) |
| `GET` | `/api/v1/prices/{id}` | Detalle completo de una moneda (nombre, precio, cambio 24h) |
| `POST` | `/api/v1/alerts` | Crea una nueva alerta (`symbol`, `target_price`, `condition`) |
| `GET` | `/api/v1/alerts` | Lista todas las alertas del usuario |
| `DELETE` | `/api/v1/alerts/{id}` | Elimina una alerta por UUID |
| `GET` | `/api/v1/history/{symbol}` | Historial de precio de los últimos 7 días |
| `GET` | `/api/v1/catalog/cryptos` | Catálogo de criptomonedas soportadas |

---

## 3. Pipeline de CI/CD

Esta es la sección central del aprendizaje técnico: la automatización del ciclo de vida del software, desde el código fuente hasta la aplicación corriendo en un entorno controlado.

> **Nota técnica:** El pipeline de Cripto Pulse está implementado mediante **Docker multi-stage builds** y **Docker Compose**, que representan la forma más portable y reproducible de definir un pipeline de despliegue. La estructura de repositorio incluye el directorio `.github/`, dejando el proyecto preparado para la incorporación de un workflow de **GitHub Actions** que automatice este mismo flujo en la nube.

### 3.1 Visión General del Pipeline

```
  [Desarrollador hace push al repositorio]
            │
            ▼
  ┌─────────────────────┐
  │  ETAPA 1: BUILD     │   docker-compose up --build
  │  (Dockerfile)       │   ────────────────────────
  │                     │   Maven descarga deps →
  │  maven:3.9.6 + JDK21│   Compila fuentes Java →
  │  mvn clean package  │   Empaqueta → app.jar
  └──────────┬──────────┘
             │
             ▼
  ┌─────────────────────┐
  │  ETAPA 2: TEST      │   (Ejecutable vía: mvn test)
  │                     │   ────────────────────────
  │  spring-boot-test   │   JUnit 5 carga contexto →
  │  contextLoads()     │   Valida autowiring de beans
  └──────────┬──────────┘
             │  ✅ Tests pasan
             ▼
  ┌─────────────────────┐
  │  ETAPA 3: PACKAGE   │   Imagen final: eclipse-temurin:21-jdk-jammy
  │  (Imagen Docker)    │   ────────────────────────
  │                     │   Solo copia el .jar generado
  │  Imagen optimizada  │   (sin Maven, sin fuentes)
  │  Puerto: 8080       │   Imagen final ~200MB vs ~500MB
  └──────────┬──────────┘
             │
             ▼
  ┌─────────────────────┐
  │  ETAPA 4: DEPLOY    │   docker-compose up -d
  │  (Docker Compose)   │   ────────────────────────
  │                     │   Levanta: criptopulse_db (PostgreSQL)
  │  Red: cripto_network│   Levanta: criptopulse_api (Spring Boot)
  │  Volumen persistente│   Flyway ejecuta migraciones V1–V5
  └─────────────────────┘
```

### 3.2 Triggers: ¿Cuándo se Dispara el Pipeline?

El pipeline se activa en dos escenarios definidos:

**Trigger Manual (desarrollo local):**
El desarrollador ejecuta explícitamente el comando en la raíz del proyecto:

```bash
docker-compose up --build -d
```

La flag `--build` fuerza la re-ejecución completa del `Dockerfile`, garantizando que cualquier cambio en el código fuente, dependencias o configuración sea incorporado en la nueva imagen.

**Trigger de Rama (preparado para CI en nube):**
La estructura de ramas del repositorio (`main` y `features-max`) sigue el patrón estándar de **Git Flow**, donde las funcionalidades se desarrollan en ramas de feature y se integran a `main` cuando están listas. Este flujo está preparado para conectar con un workflow de GitHub Actions que dispararía el pipeline automáticamente en cada `push` o `pull_request` a la rama `main`.

### 3.3 Etapa 1 — Build: El `Dockerfile` Multi-Stage

El archivo `Dockerfile` implementa una de las mejores prácticas de contenerización: el **multi-stage build**, que divide la construcción en dos etapas completamente aisladas.

```dockerfile
# --- ETAPA 1: Construcción (Build Stage) ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Runtime Stage) ---
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**¿Por qué esto importa?**

| Aspecto | Sin multi-stage | Con multi-stage |
|---|---|---|
| Imagen final contiene | Maven + JDK + fuentes + .jar | Solo JDK + .jar |
| Tamaño aproximado | ~500–700 MB | ~200–250 MB |
| Superficie de ataque | Alta (Maven expuesto) | Mínima |
| Reproducibilidad | Depende del entorno local | 100% determinista |

El comando `mvn clean package -DskipTests` dentro del `Dockerfile` asegura que la compilación siempre ocurra en un entorno limpio y controlado, independientemente de la máquina del desarrollador.

> **Nota sobre `-DskipTests`:** Los tests se omiten en la etapa de empaquetado Docker para agilizar la construcción de la imagen. En un pipeline de CI/CD completo, los tests se ejecutarían en un paso previo y separado, antes de construir la imagen, siguiendo el principio de **fail fast**.

### 3.4 Etapa 2 — Test: Validación de Calidad

La suite de pruebas está configurada con las dependencias de testing de Spring Boot en el `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

El test base `DemoCriptoApplicationTests.java` implementa la prueba de **smoke test** más fundamental en el ecosistema Spring: `contextLoads()`.

```java
@SpringBootTest
class DemoCriptoApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que TODOS los beans del contexto Spring se inicialicen
        // correctamente sin errores de configuración o inyección de dependencias.
    }
}
```

Este test valida, entre otras cosas, que:
- La conexión a la base de datos (via `DataSource`) está bien configurada.
- Flyway ejecuta todas las migraciones sin conflictos.
- Todos los `@Autowired` / inyecciones por constructor resuelven correctamente.
- Los `@Scheduled` y `@Cacheable` se registran sin errores.

Para ejecutar los tests de forma independiente:

```bash
# Desde la raíz del módulo Maven
./mvnw test

# O con Maven instalado globalmente
mvn test
```

**Herramienta complementaria de testing — Postman:**
El testing funcional e integración de los endpoints REST fue validado mediante **Postman**, permitiendo:
- Definir colecciones de requests organizadas por módulo (`Prices`, `Alerts`, `History`).
- Enviar requests `POST` con body JSON para crear alertas y verificar la respuesta `201 Created`.
- Consultar el historial de precios y validar la estructura del JSON devuelto por CoinGecko.
- Probar el flujo completo de vida de una alerta: creación → monitoreo automático → estado `TRIGGERED`.

### 3.5 Etapa 3 — Deploy: Docker Compose como Orquestador

El archivo `docker-compose.yml` define la topología completa del sistema como código (**Infrastructure as Code**), levantando dos servicios interconectados en una red privada aislada:

```yaml
services:
  db:
    image: postgres:16.13
    container_name: criptopulse_db
    environment:
      POSTGRES_DB: ${DB_NAME}      # Variables inyectadas desde .env
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5433:5432"                # Puerto externo:interno
    networks:
      - cripto_network
    volumes:
      - postgres_data:/var/lib/postgresql/data  # Persistencia entre reinicios

  api:
    build:
      context: ./demo-cripto
      dockerfile: Dockerfile       # Dispara el multi-stage build
    container_name: criptopulse_api
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${DB_NAME}
      - SPRING_DATASOURCE_USERNAME=${DB_USER}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - COINGECKO_API_KEY=${API_KEY}
    depends_on:
      - db                         # El API espera a que la BD esté lista
    networks:
      - cripto_network
```

**Decisiones de diseño clave:**

- **`depends_on: db`** — Garantiza el orden de arranque: PostgreSQL debe estar activo antes de que Spring Boot intente conectarse y ejecutar las migraciones Flyway.
- **`volumes: postgres_data`** — Los datos de la base de datos sobreviven al ciclo `docker-compose down / up`. Sin este volumen, toda la información se perdería al detener los contenedores.
- **`networks: cripto_network`** — La API se comunica con la base de datos usando el nombre del servicio (`db`) como hostname, en lugar de `localhost`. Esta es la forma correcta de comunicación entre contenedores en Docker.
- **Separación de secretos con `.env`** — Las credenciales (`DB_PASSWORD`, `API_KEY`) no están hardcodeadas en el `docker-compose.yml`. Se leen desde un archivo `.env` ignorado por Git, siguiendo las mejores prácticas de seguridad (principio de no exponer secretos en el repositorio).

### 3.6 Aseguramiento de Calidad Antes de Producción

El pipeline integra múltiples capas de validación antes de que el código llegue a un entorno productivo:

```
Calidad del código
    │
    ├── Lombok + anotaciones JPA → Menos código manual = menos errores humanos
    │
    ├── Flyway → Migraciones versionadas y atómicas; si una falla, el arranque se aborta
    │
    ├── @SpringBootTest (contextLoads) → Valida la configuración completa del contexto
    │
    ├── spring-boot-starter-webmvc-test → Disponible para pruebas de controllers con MockMvc
    │
    ├── spring-boot-starter-data-jpa-test → Disponible para pruebas de repositorios con @DataJpaTest
    │
    ├── Postman Collections → Pruebas funcionales manuales de todos los endpoints
    │
    └── Multi-stage Docker Build → Solo el artefacto (.jar) llega a producción; el
                                   código fuente y Maven nunca se exponen
```

**Evolución natural del pipeline hacia GitHub Actions:**
La estructura del proyecto está lista para incorporar el siguiente `workflow` en `.github/workflows/ci.yml`:

```yaml
name: CI/CD — Cripto Pulse

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Run Tests with Maven
        run: mvn test
        working-directory: demo-cripto/demo-cripto

      - name: Build Docker Image
        run: docker-compose build
        working-directory: demo-cripto

      - name: Push to Container Registry
        run: docker push <registry>/cripto-pulse:latest
```

Este workflow ejecutaría automáticamente las pruebas y construiría la imagen Docker en cada `push` a `main`, bloqueando cualquier merge que rompa los tests.

---

## 4. Reflexión sobre el Aprendizaje

El desarrollo de Cripto Pulse representó una experiencia formativa integral al demostrar, en la práctica, cómo los principios de ingeniería de software se interconectan para producir un sistema robusto y mantenible. La implementación de una **arquitectura en capas** no fue simplemente un requisito estructural: fue el mecanismo que permitió modificar la lógica de negocio en `AlertMonitor` o agregar un nuevo endpoint en `CryptoCatalogController` sin afectar el resto del sistema, evidenciando el valor real del principio de responsabilidad única. La gestión del esquema relacional mediante **migraciones Flyway versionadas** introdujo la disciplina de tratar la base de datos como código, garantizando que el estado del esquema sea siempre predecible y reproducible en cualquier entorno, desde el desarrollo local hasta producción.

El uso de **Postman** como herramienta de testing funcional durante el desarrollo complementó las pruebas automatizadas al permitir validar el contrato de la API desde la perspectiva del consumidor: verificar que los códigos HTTP sean correctos (`201 Created`, `204 No Content`, `404 Not Found`), que el formato JSON de respuesta sea el esperado y que el flujo completo de una alerta —creación, monitoreo automático por el `Scheduler` y cambio de estado a `TRIGGERED`— funcionara de extremo a extremo. La automatización del despliegue mediante el **pipeline Dockerfile multi-stage + Docker Compose** demostró que el valor de CI/CD no reside únicamente en la velocidad, sino en la **confianza**: saber que cada vez que se ejecuta `docker-compose up --build`, el artefacto que se despliega es exactamente lo que el repositorio contiene, compilado y testeado en un entorno limpio, sin dependencias implícitas de la máquina del desarrollador. Esta combinación de arquitectura limpia, testing en capas y despliegue automatizado constituye la base profesional sobre la que se construyen sistemas escalables y confiables en la industria.

---

*Documento generado como parte del portafolio técnico del proyecto Cripto Pulse · Java 21 + Spring Boot 4 + PostgreSQL 16 + Docker*
