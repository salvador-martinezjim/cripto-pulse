package uv.mx.demo_cripto.service;

import org.springframework.stereotype.Service;
import uv.mx.demo_cripto.dto.AlertRequest;
import uv.mx.demo_cripto.dto.AlertResponse;
import uv.mx.demo_cripto.entity.Alert;
import uv.mx.demo_cripto.repository.AlertRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertResponse createAlert(AlertRequest request) {
        Alert alert = new Alert();
        // Le asignamos el ID temporal que Max agregó en el YAML
        alert.setUserId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        alert.setCryptoSymbol(request.getSymbol());
        alert.setTargetPrice(request.getTarget_price());
        alert.setCondition(request.getCondition());
        alert.setStatus("ACTIVE");
        
        Alert savedAlert = alertRepository.save(alert);

        return AlertResponse.builder()
                .id(savedAlert.getId())
                .status(savedAlert.getStatus())
                .created_at(savedAlert.getCreatedAt())
                .symbol(savedAlert.getCryptoSymbol())
                .condition(savedAlert.getCondition())
                .target_price(savedAlert.getTargetPrice())
                .build();
    }

    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll().stream().map(alert -> 
            AlertResponse.builder()
                .id(alert.getId())
                .status(alert.getStatus())
                .created_at(alert.getCreatedAt())
                .symbol(alert.getCryptoSymbol())
                .condition(alert.getCondition())
                .target_price(alert.getTargetPrice())
                .build()
        ).collect(Collectors.toList());
    }

    public void deleteAlert(UUID id) {
        alertRepository.deleteById(id);
    }
}