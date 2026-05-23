package uv.mx.demo_cripto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Configuramos la BD en memoria y apagamos Flyway para que las pruebas no fallen en GitHub Actions
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.flyway.enabled=false"
    }
)
class DemoCriptoApplicationTests {

    @Autowired
    private Environment env;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void testGetHistoryEndpoint() {
        String port = env.getProperty("local.server.port");
        String url = "http://localhost:" + port + "/api/v1/history/BTC";
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetHistoryEthEndpoint() {
        String port = env.getProperty("local.server.port");
        String url = "http://localhost:" + port + "/api/v1/history/ETH";
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateAlertEndpoint() {
        String port = env.getProperty("local.server.port");
        String url = "http://localhost:" + port + "/api/v1/alerts";
        
        String jsonBody = "{\"symbol\": \"BTC\", \"condition\": \"ABOVE\", \"target_price\": 65000}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}