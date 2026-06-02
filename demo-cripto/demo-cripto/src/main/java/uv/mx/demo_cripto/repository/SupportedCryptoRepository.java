package uv.mx.demo_cripto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uv.mx.demo_cripto.entity.SupportedCrypto;

public interface SupportedCryptoRepository extends JpaRepository<SupportedCrypto, Long> {
}