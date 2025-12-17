package com.engine.config;

import com.engine.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configuración de CSRF (Desactivar para facilitar APIs de juegos, activar si es solo web)
            .csrf(csrf -> csrf.disable())

            // 2. Autorización de Rutas
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y vistas públicas
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                // Permitimos editor y play a anónimos (validado en la vista con Thymeleaf)
                .requestMatchers("/editor/**", "/play/**").permitAll()
                // Cualquier otra cosa requiere autenticación
                .anyRequest().authenticated()
            )

            // 3. Configuración de Login
            .formLogin(form -> form
                .loginPage("/login")           // Ruta de nuestro controlador (muestra index.html)
                .loginProcessingUrl("/login")  // Ruta donde Spring Security espera el POST del formulario
                .defaultSuccessUrl("/", true)  // Redirigir al inicio tras login exitoso
                .failureUrl("/login?error=true") // Redirigir si falla
                .permitAll()
            )

            // 4. Configuración de Logout (Sin guardar memoria)
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true) // Borra la sesión de memoria
                .deleteCookies("JSESSIONID") // Limpia la cookie
                .permitAll()
            );

        return http.build();
    }

    // Proveedor de autenticación (Conecta DB con Security)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Encriptador de contraseñas (BCrypt es el estándar seguro)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}