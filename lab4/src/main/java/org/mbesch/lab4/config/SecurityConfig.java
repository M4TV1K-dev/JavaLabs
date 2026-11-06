package org.mbesch.lab4.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers("/h2-console/**", "/api/auth/register")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // Открытые эндпоинты: регистрация, получение CSRF-токена, веб-консоль H2
                .requestMatchers("/api/auth/register", "/api/csrf", "/h2-console/**").permitAll()
                // Публичный просмотр каталога животных и вольеров
                .requestMatchers(HttpMethod.GET, "/api/animals/**", "/api/enclosures/**").permitAll()

                // Подача заявки на усыновление доступна зарегистрированным пользователям (USER и ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/operations/submit-application").hasAnyRole("USER", "ADMIN")

                // Остальные бизнес-операции приюта: строго администратор
                .requestMatchers("/api/operations/**").hasRole("ADMIN")

                // Заявки на усыновление: просмотр доступен пользователям (своих) и администраторам
                .requestMatchers("/api/adoption-applications/**").hasAnyRole("USER", "ADMIN")

                // Изменение животных и вольеров: строго администратор
                .requestMatchers(HttpMethod.POST, "/api/animals/**", "/api/enclosures/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/animals/**", "/api/enclosures/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/animals/**", "/api/enclosures/**").hasRole("ADMIN")

                // Акты передачи: строго администратор
                .requestMatchers("/api/animal-handover-records/**").hasRole("ADMIN")

                // Управление пользователями: строго администратор
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\": \"Ошибка аутентификации: неверные учетные данные или учетная запись заблокирована\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\": \"Доступ запрещен: недостаточно полномочий или недействителен CSRF-токен\"}");
                })
            );

        return http.build();
    }
}
