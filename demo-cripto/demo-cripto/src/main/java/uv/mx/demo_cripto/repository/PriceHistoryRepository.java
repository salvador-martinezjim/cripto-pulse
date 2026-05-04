package uv.mx.demo_cripto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uv.mx.demo_cripto.entity.PriceHistory;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    
    List<PriceHistory> findByCryptoSymbolOrderByRecordedAtAsc(String cryptoSymbol);

    List<PriceHistory> findByCryptoSymbolAndRecordedAtBetweenOrderByRecordedAtAsc(
            String cryptoSymbol, LocalDateTime from, LocalDateTime to);
}