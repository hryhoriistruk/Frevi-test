package HireCraft.com.SpringBoot.security.config;

import HireCraft.com.SpringBoot.exceptions.RestAccessDeniedHandler;
import HireCraft.com.SpringBoot.exceptions.RestAuthenticationEntryPoint;
import HireCraft.com.SpringBoot.exceptions.RestAuthenticationFailureHandler;
import HireCraft.com.SpringBoot.security.jwt.JwtAuthenticationFilter;
import HireCraft.com.SpringBoot.security.user.CustomUserDetailsService;
import HireCraft.com.SpringBoot.security.jwt.JwtAuthorizationFilter;
import HireCraft.com.SpringBoot.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableMethodSecurity
@Configuration
//@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAuthenticationFailureHandler failureHandler;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService, RestAccessDeniedHandler accessDeniedHandler, RestAuthenticationEntryPoint authEntryPoint, RestAuthenticationFailureHandler failureHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authEntryPoint = authEntryPoint;
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authManager, jwtTokenProvider);
        authFilter.setFilterProcessesUrl("/api/v1/auth/login");
        authFilter.setAuthenticationFailureHandler(failureHandler);

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password"
                        ).permitAll()
                                .anyRequest().authenticated()
//                        .requestMatchers(HttpMethod.GET,  "/api/v1/users/me").authenticated()
//                        .requestMatchers(HttpMethod.DELETE,  "/api/v1/users/delete/{id}").authenticated()
//                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/profile").authenticated()
//                        .requestMatchers(HttpMethod.POST, "api/v1/bookings/create").authenticated()
//                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/update-profile-picture").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/bookings/provider/{providerId}").authenticated()
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/bookings/{bookingId}/status").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/bookings/client/me").authenticated()
//                        // Only users with the ADD_REVIEW permission
//                        .requestMatchers(HttpMethod.POST, "/api/v1/reviews/create-review").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/all").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/provider/{providerId}").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/client/{clientId}").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/client/{clientId}/provider/{providerId}").authenticated()
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/messages/booking/{bookingId}").authenticated()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/messages/send").authenticated()
//
//                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/update-profile").authenticated()
//                        // all other calls require MANAGE_USERS
//                        .anyRequest().hasAuthority("MANAGE_USERS")
                )
                .addFilter(authFilter)
                .addFilterBefore(
                        new JwtAuthorizationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // your frontend dev URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // needed if using Authorization header or cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}