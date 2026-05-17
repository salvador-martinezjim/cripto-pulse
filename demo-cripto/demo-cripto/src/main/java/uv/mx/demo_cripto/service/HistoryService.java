package uv.mx.demo_cripto.service;

import org.springframework.stereotype.Service;
import uv.mx.demo_cripto.entity.PriceHistory;
import uv.mx.demo_cripto.repository.PriceHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {

    private final PriceHistoryRepository repository;

    public HistoryService(PriceHistoryRepository repository) {
        this.repository = repository;
    }

    public List<PriceHistory> getHistory(String symbol, LocalDateTime from, LocalDateTime to) {
        // Si el usuario nos mandó ambas fechas, usamos la consulta con filtro
        if (from != null && to != null) {
            return repository.findByCryptoSymbolAndRecordedAtBetweenOrderByRecordedAtAsc(symbol, from, to);
        }
        
        // Si no mandó fechas, le devolvemos todo el historial de esa moneda
        return repository.findByCryptoSymbolOrderByRecordedAtAsc(symbol);
    }
}