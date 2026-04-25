package uv.mx.demo_cripto.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // Magia de Lombok: crea Getters, Setters y constructores automáticamente
@Entity // Le dice a Java que esta clase es una tabla de base de datos
@Table(name = "price_history") // El nombre exacto de tu tabla en PostgreSQL
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crypto_symbol")
    private String cryptoSymbol;

    private BigDecimal price;

    @Column(name = "recorded_at", insertable = false, updatable = false)
    private LocalDateTime recordedAt; 
    // Usamos insertable=false porque PostgreSQL pone la fecha automáticamente con CURRENT_TIMESTAMP
}