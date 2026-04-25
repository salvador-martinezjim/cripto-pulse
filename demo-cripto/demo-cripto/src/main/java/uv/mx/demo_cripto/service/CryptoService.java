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
    private final PriceHistoryRepository repository; // <-- Inyectamos tu nueva base de datos
    
    private final String COINGECKO_URL = "https://api.coingecko.com/api/v3/simple/price?ids={symbols}&vs_currencies=usd";

    // Spring Boot nos entrega el RestTemplate y el Repositorio automáticamente
    public CryptoService(RestTemplate restTemplate, PriceHistoryRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public Map<String, Object> getPrices(String symbols) {
        // 1. El cocinero va a internet por los datos (CoinGecko)
        Map<String, Map<String, Number>> response = restTemplate.getForObject(COINGECKO_URL, Map.class, symbols);

        if (response != null) {
            // 2. Recorremos el JSON que nos dio CoinGecko
            response.forEach((cryptoSymbol, priceData) -> {
                if (priceData != null && priceData.containsKey("usd")) {
                    
                    // Extraemos el precio y lo convertimos a un formato decimal exacto
                    BigDecimal currentPrice = new BigDecimal(priceData.get("usd").toString());

                    // 3. Creamos una nueva "Fila" para nuestra tabla en Java
                    PriceHistory history = new PriceHistory();
                    history.setCryptoSymbol(cryptoSymbol);
                    history.setPrice(currentPrice);

                    // 4. ¡LA MAGIA! Guardamos en PostgreSQL de forma permanente
                    repository.save(history);
                    
                    // Imprimimos en la terminal para que veas que funcionó
                    System.out.println("✅ Guardado en BD: " + cryptoSymbol + " -> $" + currentPrice);
                }
            });
        }

        // 5. El cocinero le entrega la comida al mesero (Controlador)
        return (Map) response;
    }
}