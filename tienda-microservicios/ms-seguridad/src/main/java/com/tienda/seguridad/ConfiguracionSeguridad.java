package com.tienda.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfiguracionSeguridad {

    @Bean
    public UserDetailsService usuarios() {

        UserDetails admin1 = User.withUsername("admin1")
                .password("{noop}123")
                .roles("PEDIDOS")
                .build();

        UserDetails admin2 = User.withUsername("admin2")
                .password("{noop}123")
                .roles("VENTAS")
                .build();

        UserDetails admin3 = User.withUsername("admin3")
                .password("{noop}123")
                .roles("PRODUCTOS")
                .build();

        return new InMemoryUserDetailsManager(admin1, admin2, admin3);
    }

@Bean
public SecurityFilterChain seguridad(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/error").permitAll()
            .requestMatchers("/", "/pedidos/**").hasRole("PEDIDOS")
            .requestMatchers("/ventas/**").hasRole("VENTAS")
            .requestMatchers("/productos/**").hasRole("PRODUCTOS")
            .anyRequest().denyAll()
        )
        .formLogin(form -> form.permitAll())
        .exceptionHandling(ex -> ex
            .accessDeniedHandler((request, response, exception) -> {
                response.setStatus(403);
                response.getWriter().write("Acceso denegado");
            })
        );

    return http.build();
}
}
