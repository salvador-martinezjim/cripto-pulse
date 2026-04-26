package uv.mx.demo_cripto.service;

import org.springframework.stereotype.Service;
import uv.mx.demo_cripto.dto.AlertRequest;
import uv.mx.demo_cripto.dto.AlertResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

    private final List<AlertResponse> alerts = new ArrayList<>();

    public AlertResponse createAlert(AlertRequest request) {
        AlertResponse alert = AlertResponse.builder()
                .id(UUID.randomUUID())
                .status("active")
                .created_at(LocalDateTime.now())
                .symbol(request.getSymbol())
                .condition(request.getCondition())
                .target_price(request.getTarget_price())
                .build();

        alerts.add(alert);
        return alert;
    }

    public List<AlertResponse> getAllAlerts() {
        return alerts;
    }
}