package uv.mx.demo_cripto.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uv.mx.demo_cripto.entity.PriceHistory;
import uv.mx.demo_cripto.service.HistoryService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/history") // La ruta base según tu README
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    // Ruta final: GET /api/v1/history/bitcoin?from=...&to=...
    @GetMapping("/{symbol}")
    public ResponseEntity<List<PriceHistory>> getCryptoHistory(
            @PathVariable String symbol, // Atrapa el 'bitcoin' o 'ethereum' de la URL
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, // Atrapa la fecha de inicio
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to // Atrapa la fecha de fin
    ) {
        // Le pedimos al cocinero el historial
        List<PriceHistory> history = historyService.getHistory(symbol, from, to);
        
        // Si no hay datos, podemos devolver un 404 (Not Found) o una lista vacía.
        // Una lista vacía con un 200 OK es una buena práctica aquí.
        return ResponseEntity.ok(history);
    }
}