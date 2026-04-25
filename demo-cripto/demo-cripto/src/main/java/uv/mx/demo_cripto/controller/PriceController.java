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
}