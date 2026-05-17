package uv.mx.demo_cripto;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Abre las puertas de TODOS los endpoints de tu API (/alerts, /prices, etc.)
                .allowedOrigins("*") // Permite que CUALQUIER frontend se conecte (ideal para desarrollo local)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite todos los métodos HTTP
                .allowedHeaders("*"); // Permite que React envíe cualquier tipo de dato (como JSON)
    }
}