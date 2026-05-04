package uv.mx.demo_cripto.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Data
@Builder
public class AlertResponse {
    private UUID id;
    private String status;
    private LocalDateTime created_at;
    private String symbol;
    private String condition;
    private BigDecimal target_price;
}