package uv.mx.demo_cripto.service;

import java.util.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uv.mx.demo_cripto.entity.PriceHistory;
import uv.mx.demo_cripto.repository.PriceHistoryRepository;

import java.math.BigDecimal;

@Service
public class CryptoService {

    private final RestTemplate restTemplate;
    private final PriceHistoryRepository repository; 
    
    private final String COINGECKO_URL = "https://api.coingecko.com/api/v3/simple/price?ids={symbols}&vs_currencies=usd";

    public CryptoService(RestTemplate restTemplate, PriceHistoryRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    // ==========================================================
    // RECETA 1: Obtener varios precios y guardar en BD (El que ya teníamos)
    // ==========================================================
    public Map<String, Object> getPrices(String symbols) {
    Map<String, Map<String, Number>> response = restTemplate.getForObject(COINGECKO_URL, Map.class, symbols);

    if (response != null) {
        response.forEach((cryptoSymbol, priceData) -> {
            if (priceData != null && priceData.containsKey("usd")) {
                BigDecimal currentPrice = new BigDecimal(priceData.get("usd").toString());

                PriceHistory history = new PriceHistory();
                history.setCryptoSymbol(cryptoSymbol);
                history.setPrice(currentPrice);

                // --- 🛡️ INICIA EL CHALECO ANTIBALAS ---
                try {
                    repository.save(history);
                    System.out.println("✅ Guardado en BD: " + cryptoSymbol + " -> $" + currentPrice);
                } catch (Exception e) {
                    // Si la base de datos rechaza la moneda (ej. Solana), lo ignoramos elegantemente
                    System.out.println("⚠️ Aviso (Ignorado): No se pudo guardar historial para " + cryptoSymbol);
                }
                // --- 🛡️ TERMINA EL CHALECO ANTIBALAS ---
            }
        });
    }
    
    // (Asegúrate de dejar el return que ya tenías aquí abajo)
    return (Map<String, Object>) (Map<?, ?>) response;
}

    // ==========================================================
    // RECETA 2: ¡EL NUEVO! Detalle de una sola moneda (Para la Fase 3)
    // ==========================================================
    public Map<String, Object> getCryptoDetails(String coinId) {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null) {
                Map<String, Object> marketData = (Map<String, Object>) response.get("market_data");
                Map<String, Object> currentPrices = (Map<String, Object>) marketData.get("current_price");
                
                return Map.of(
                    "name", response.get("name"), 
                    "symbol", response.get("symbol").toString().toUpperCase(), 
                    "current_price", currentPrices.get("usd"), 
                    "change_24h", marketData.get("price_change_percentage_24h") 
                );
            }
        } catch (Exception e) {
            return Map.of("error", "Criptomoneda '" + coinId + "' no encontrada.");
        }
        
        return Map.of("error", "No se pudo obtener la información.");

    }

    // Nuevo método para obtener el historial de precios (Últimos 7 días)
    @Cacheable(value = "history", key = "#symbol")
    public List<Map<String, Object>> getMarketHistory(String symbol) {
        // CoinGecko endpoint para el historial del mercado (precios, market cap, volumen)
        String url = "https://api.coingecko.com/api/v3/coins/" + symbol.toLowerCase() + "/market_chart?vs_currency=usd&days=7";

        try {
            RestTemplate restTemplate = new RestTemplate();
            // Recibimos la respuesta de CoinGecko como un Mapa genérico
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("prices")) {
                // CoinGecko devuelve los precios como un arreglo de arreglos: [ [timestamp, precio], [timestamp, precio] ]
                List<List<Number>> prices = (List<List<Number>>) response.get("prices");
                
                // Lo transformamos al formato que tu React (y tu tabla de endpoints) espera: [{"timestamp": x, "price": y}]
                List<Map<String, Object>> formattedHistory = new ArrayList<>();

                for (List<Number> point : prices) {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("timestamp", point.get(0));
                    dataPoint.put("price", point.get(1));
                    formattedHistory.add(dataPoint);
                }
                
                return formattedHistory;
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo historial de CoinGecko: " + e.getMessage());
        }
        
        return Collections.emptyList(); // Si falla, devolvemos una lista vacía
    }
}

