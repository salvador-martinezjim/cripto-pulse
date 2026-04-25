package uv.mx.demo_cripto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uv.mx.demo_cripto.entity.PriceHistory;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    // ¡Literalmente está vacío! 
    // Al extender JpaRepository, Java nos regala métodos como save(), findAll(), findById(), etc.
}