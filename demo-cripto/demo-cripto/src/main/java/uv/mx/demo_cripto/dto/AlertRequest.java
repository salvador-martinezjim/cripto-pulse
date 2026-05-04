package uv.mx.demo_cripto.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AlertRequest {
    private String symbol;
    private BigDecimal target_price;
    private String condition;
}