package uv.mx.demo_cripto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uv.mx.demo_cripto.dto.AlertRequest;
import uv.mx.demo_cripto.dto.AlertResponse;
import uv.mx.demo_cripto.service.AlertService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@RequestBody AlertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(request));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}