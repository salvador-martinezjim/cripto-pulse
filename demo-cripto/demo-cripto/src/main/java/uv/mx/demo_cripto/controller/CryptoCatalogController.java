package uv.mx.demo_cripto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uv.mx.demo_cripto.entity.SupportedCrypto;
import uv.mx.demo_cripto.repository.SupportedCryptoRepository;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/catalog")
public class CryptoCatalogController {

    @Autowired
    private SupportedCryptoRepository cryptoRepository;

    @GetMapping("/cryptos")
    public ResponseEntity<List<SupportedCrypto>> getSupportedCryptos() {
        return ResponseEntity.ok(cryptoRepository.findAll());
    }
}