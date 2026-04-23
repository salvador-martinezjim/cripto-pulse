# Cripto Pulse 📈

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)

##  Descripción del Proyecto
**Cripto Pulse** es una plataforma diseñada para resolver la dificultad de monitorear el mercado de criptomonedas de manera sencilla y automatizada. La API permite a pequeños inversionistas y estudiantes realizar un seguimiento de precios en tiempo real, mantener un historial persistente para análisis de tendencias y configurar alertas personalizadas sin depender de plataformas complejas o costosas.

Este proyecto aplica competencias reales de ingeniería, incluyendo el diseño de arquitecturas orientadas a servicios, persistencia de datos relacionales y despliegue automatizado mediante pipelines de CI/CD.

---

## Edpoins
### 1. Monitoreo de Precios

| Método | Ruta | Parámetros | Descripción | Respuesta (JSON) |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/prices` | `?symbols=btc,eth` | Obtiene el precio actual de criptos específicas. | `[{"symbol": "BTC", "price": 65000.50}, ...]` |
| **GET** | `/api/v1/prices/{symbol}` | `path: symbol` | Detalle completo de una moneda en particular. | `{"name": "Bitcoin", "symbol": "BTC", "change_24h": 2.5...}` |

### 2. Alertas Personalizadas

| Método | Ruta | Parámetros | Descripción | Respuesta (JSON) |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/alerts` | **Body:** `symbol, target_price, condition` | Crea una nueva alerta de precio. | `{"id": 1, "status": "active", "created_at": "..."}` |
| **GET** | `/api/v1/alerts` | N/A | Lista todas las alertas configuradas por el usuario. | `[{"id": 1, "symbol": "BTC", "target": 70000}, ...]` |
| **DELETE**| `/api/v1/alerts/{id}` | `path: id` | Elimina una alerta existente. | `204 No Content` |

### 3. Historial y Análisis

| Método | Ruta | Parámetros | Descripción | Respuesta (JSON) |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/history/{symbol}`| `?from=date&to=date`| Obtiene el historial de precios para gráficas. | `[{"timestamp": "...", "price": 64000}, ...]` |

##  Arquitectura del Sistema
El sistema sigue una arquitectura de capas (N-Tier) y se despliega utilizando contenedores para asegurar la paridad entre los entornos de desarrollo y producción.

```mermaid
graph TD
    User((Usuario/Cliente)) -->|Peticiones REST| API[Cripto Pulse API - Spring Boot]
    API -->|Persistencia| DB[(PostgreSQL)]
    API -->|Consulta Precios| ExtAPI[Servicios Externos: CoinGecko/Binance]
    
    subgraph "Infraestructura (Docker)"
    API
    DB
    end
    
    subgraph "Automatización"
    GHA[GitHub Actions] -->|CI/CD| API
    end
