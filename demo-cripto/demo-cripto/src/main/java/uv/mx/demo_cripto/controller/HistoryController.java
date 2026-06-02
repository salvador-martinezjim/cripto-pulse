package uv.mx.demo_cripto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uv.mx.demo_cripto.service.CryptoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/history") // Tu ruta base intacta
public class HistoryController {

    @Autowired
    private CryptoService cryptoService; // Usamos el servicio que va a CoinGecko

    // Ruta final: GET /api/v1/history/{symbol}
    @GetMapping("/{symbol}")
    public ResponseEntity<List<Map<String, Object>>> getCryptoHistory(
            @PathVariable String symbol, 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to 
    ) {
        // Por ahora ignoramos los parámetros 'from' y 'to' y le pedimos al servicio 
        // los últimos 7 días de datos reales de CoinGecko
        List<Map<String, Object>> history = cryptoService.getMarketHistory(symbol);
        
        if (history == null || history.isEmpty()) {
            return ResponseEntity.notFound().build(); // Si falla CoinGecko, mandamos 404
        }
        
        return ResponseEntity.ok(history); // Devolvemos el JSON exacto que espera React
    }
}