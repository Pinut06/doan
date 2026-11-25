package com.example.phukien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.requireCsrfProtectionMatcher(new OrRequestMatcher(
                    new AntPathRequestMatcher("/admin/don-hang/*/thanh-toan", "POST"),
                    new AntPathRequestMatcher("/admin/don-hang/*/giao-hang", "POST"),
                    new AntPathRequestMatcher("/admin/don-hang/*/doi-tra/**", "POST"),
                    new AntPathRequestMatcher("/admin/don-hang/*/hoa-don/**", "POST"),
                    new AntPathRequestMatcher("/don-hang/*/doi-tra", "POST"),
                    new AntPathRequestMatcher("/don-hang/*/da-nhan", "POST"))))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/gio-hang/**", "/thanh-toan/**", "/don-hang/**", "/binh-luan/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/san-pham/*/binh-luan").authenticated()
                .requestMatchers("/quen-mat-khau").permitAll()
                .anyRequest().permitAll())
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    boolean laAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    response.sendRedirect(request.getContextPath() + (laAdmin ? "/admin" : "/"));
                })
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/?dangxuat")
                .permitAll());
        return http.build();
    }
}
