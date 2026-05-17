package uv.mx.demo_cripto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AlertMonitor {

    @Autowired
    private CryptoService cryptoService;

    // Aquí después inyectaremos tu AlertRepository
    // @Autowired
    // private AlertRepository alertRepository;

    // Esta etiqueta mágica hace que el método se ejecute solo cada 60,000 milisegundos (1 minuto)
    @Scheduled(fixedRate = 60000)
    public void monitorPriceAlerts() {
        System.out.println("\n🤖 [" + LocalDateTime.now() + "] CRON JOB: Despertando para revisar alertas...");
        
        try {
            // Ejemplo de lo que el robot hará: 
            // 1. Obtener precio actual (Ej. Bitcoin)
            System.out.println("   -> Consultando precio actual en CoinGecko...");
            // List<Map<String, Object>> currentData = cryptoService.getMarketHistory("bitcoin");
            // (Aquí usaríamos tu método para obtener el precio actual exacto)

            // 2. Aquí buscarás en la BD: 
            // List<Alert> pendingAlerts = alertRepository.findByStatus("PENDING");

            // 3. Compararías el precio y lanzarías notificación...
            System.out.println("   -> Alertas revisadas exitosamente. Todo en orden.");
            
        } catch (Exception e) {
            System.err.println("❌ Error en el monitor de alertas: " + e.getMessage());
        }
        
        System.out.println("💤 CRON JOB: Volviendo a dormir...\n");
    }
}