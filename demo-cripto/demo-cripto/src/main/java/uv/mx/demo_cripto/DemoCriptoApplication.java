package uv.mx.demo_cripto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DemoCriptoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoCriptoApplication.class, args);
    }

    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
