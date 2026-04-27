package uv.mx.demo_cripto.service;

import org.springframework.stereotype.Service;
import uv.mx.demo_cripto.repository.AlertRepository;
import java.util.UUID;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void deleteAlert(UUID id) {
        alertRepository.deleteById(id);
    }
}