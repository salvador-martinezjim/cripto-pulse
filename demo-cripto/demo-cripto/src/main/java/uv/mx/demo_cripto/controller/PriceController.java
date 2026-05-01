package uv.mx.demo_cripto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uv.mx.demo_cripto.service.CryptoService;
import java.util.Map;

@RestController 
@RequestMapping("/api/v1/prices") // Ruta del proyecto
public class PriceController {

    private final CryptoService cryptoService;

    public PriceController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrentPrices(
            @RequestParam(defaultValue = "bitcoin,ethereum") String symbols) {
        
        // El mesero le pide la comida al cocinero
        Map<String, Object> prices = cryptoService.getPrices(symbols);
        
        // Entregamos la orden exitosa (Código 200 OK)
        return ResponseEntity.ok(prices);
    }

    // NUEVO ENDPOINT: GET /api/v1/prices/{symbol}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCryptoDetails(@PathVariable String id) {
        
        // El mesero le pide los detalles al cocinero (pasamos el texto a minúsculas por seguridad)
        Map<String, Object> details = cryptoService.getCryptoDetails(id.toLowerCase());
        
        // Validamos si el cocinero nos regresó un error
        if (details.containsKey("error")) {
            return ResponseEntity.status(404).body(details); // Devolvemos error 404 Not Found
        }
        
        // Entregamos la orden exitosa (Código 200 OK)
        return ResponseEntity.ok(details);
    }
}