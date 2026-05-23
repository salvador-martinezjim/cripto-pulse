package uv.mx.demo_cripto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uv.mx.demo_cripto.entity.Alert;
import uv.mx.demo_cripto.repository.AlertRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AlertMonitor {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private AlertRepository alertRepository;

    @Scheduled(fixedRate = 30000)
    public void monitorPriceAlerts() {
        System.out.println("\n🤖 [" + LocalDateTime.now() + "] CRON JOB: Despertando para revisar alertas...");
        
        try {
            List<Alert> allAlerts = alertRepository.findAll();
            System.out.println("   -> Alertas totales encontradas en la BD: " + allAlerts.size());
            
            // Filtramos las que NO digan TRIGGERED (aceptamos nulos, ACTIVE, PENDING, etc)
            List<Alert> pendingAlerts = allAlerts.stream()
                .filter(a -> a.getStatus() == null || !a.getStatus().equalsIgnoreCase("TRIGGERED"))
                .collect(Collectors.toList());

            System.out.println("   -> Alertas que necesitan revisión (no disparadas): " + pendingAlerts.size());

            if (pendingAlerts.isEmpty()) {
                System.out.println("   -> No hay alertas pendientes. Todo tranquilo.");
            } else {
                
                // MODO ESPÍA: Imprimimos qué monedas vamos a buscar
                for(Alert a : pendingAlerts) {
                    System.out.println("      - Revisando: " + a.getCryptoSymbol() + " | Objetivo: $" + a.getTargetPrice() + " | Condición: " + a.getCondition());
                }

                String symbolsToFetch = pendingAlerts.stream()
                    .map(a -> a.getCryptoSymbol().toLowerCase())
                    .distinct()
                    .collect(Collectors.joining(","));

                System.out.println("   -> Consultando a CoinGecko precios de: " + symbolsToFetch);
                
                Map<String, Object> livePrices = cryptoService.getPrices(symbolsToFetch);

                for (Alert alert : pendingAlerts) {
                    String symbol = alert.getCryptoSymbol().toLowerCase();
                    
                    if (livePrices.containsKey(symbol)) {
                        @SuppressWarnings("unchecked")
                        Map<String, Number> priceData = (Map<String, Number>) livePrices.get(symbol);
                        
                        if (priceData != null && priceData.containsKey("usd")) {
                            BigDecimal currentPrice = new BigDecimal(priceData.get("usd").toString());
                            BigDecimal targetPrice = alert.getTargetPrice();
                            boolean conditionMet = false;

                            if ("ABOVE".equalsIgnoreCase(alert.getCondition()) && currentPrice.compareTo(targetPrice) >= 0) {
                                conditionMet = true;
                            } else if ("BELOW".equalsIgnoreCase(alert.getCondition()) && currentPrice.compareTo(targetPrice) <= 0) {
                                conditionMet = true;
                            }

                            if (conditionMet) {
                                alert.setStatus("TRIGGERED");
                                alertRepository.save(alert);
                                System.out.println("   🔔 ¡BINGO! Alerta disparada para " + symbol.toUpperCase() + " (Precio: $" + currentPrice + ")");
                            } else {
                                System.out.println("   ⏳ Aún no se cumple para " + symbol.toUpperCase() + " (Actual: $" + currentPrice + " | Meta: $" + targetPrice + ")");
                            }
                        }
                    } else {
                        System.out.println("   ⚠️ No se encontraron precios en CoinGecko para: " + symbol);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el monitor de alertas: " + e.getMessage());
        }
        
        System.out.println("💤 CRON JOB: Volviendo a dormir...\n");
    }
}