package uv.mx.demo_cripto.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uv.mx.demo_cripto.entity.PriceHistory;
import uv.mx.demo_cripto.repository.PriceHistoryRepository;

import java.math.BigDecimal;
import java.util.Map;

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

                    repository.save(history);
                    System.out.println("✅ Guardado en BD: " + cryptoSymbol + " -> $" + currentPrice);
                }
            });
        }
        return (Map) response;
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
}