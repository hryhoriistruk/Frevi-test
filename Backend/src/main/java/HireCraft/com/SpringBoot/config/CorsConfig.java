package HireCraft.com.SpringBoot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // Apply CORS to all endpoints under /api/
                .allowedOrigins("http://localhost:5173") // Allow requests from your frontend origin
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Allow all necessary HTTP methods, especially PATCH and OPTIONS
                .allowedHeaders("*") // Allow all headers, including Authorization
                .allowCredentials(true) // Allow sending of cookies and authentication headers
                .maxAge(3600); // Cache the preflight request for 1 hour (optional, but good for performance)
    }
}
