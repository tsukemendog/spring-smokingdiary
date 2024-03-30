package io.github.tsukemendog.nondesire.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;



@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final OAuthSuccessHandler oAuthSuccessHandler;
    /* 모든 uri 에 대하여 접근 권한 요구 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers()
                .frameOptions()
                .disable()
                .and()

                .authorizeHttpRequests((authorize) -> authorize
                        .mvcMatchers("/", "/css/**", "/js/**", "/.well-known/**",
                         "/image/**","/favicon.ico","/robots.txt","/oauth2-login", "/diary", "/remain-time", "/logo192.png", "/logo512.png", "/manifest.json")
                        .permitAll().anyRequest().authenticated()
                ) //.authenticate() 로 변경

                .formLogin().disable()
                .httpBasic().disable()
                .addFilter(corsFilter())
                .csrf().disable()

                .oauth2Login().loginPage("/oauth2-login").successHandler(oAuthSuccessHandler);
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //config.setAllowCredentials(true);  //credential 요청은 아니기에 false
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }
}