package uv.mx.demo_cripto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uv.mx.demo_cripto.service.AlertService;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}