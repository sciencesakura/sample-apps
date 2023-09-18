package com.sciencesakura.sample.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
class SecurityConfig {

  private static final String USERNAME_PARAMETER = "email";

  private static final String PASSWORD_PARAMETER = "passwd";

  private final ObjectMapper objectMapper;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http,
      @Value("${server.servlet.session.cookie.name:JSESSIONID}") String cookie) throws Exception {
    http.formLogin(login -> login
        .usernameParameter(USERNAME_PARAMETER)
        .passwordParameter(PASSWORD_PARAMETER)
        .successHandler(authenticationSuccessHandler())
        .failureHandler(authenticationFailureHandler())
        .permitAll());
    http.logout(logout -> logout
        .clearAuthentication(true)
        .deleteCookies(cookie)
        .logoutSuccessHandler(logoutSuccessHandler()));
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/s/**").hasRole("ADMIN")
        .requestMatchers("/**").hasRole("USER"));
    http.exceptionHandling(ex -> ex
        .authenticationEntryPoint(authenticationEntryPoint())
        .accessDeniedHandler(accessDeniedHandler()));
    http.csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

  private AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (request, response, authentication) -> {
      log.info("signed in successfully: {}", request.getParameter(USERNAME_PARAMETER));
      if (response.isCommitted()) {
        return;
      }
      response.setStatus(HttpStatus.NO_CONTENT.value());
    };
  }

  private AuthenticationFailureHandler authenticationFailureHandler() {
    return (request, response, exception) -> {
      log.info("failed to sign in: {}", request.getParameter(USERNAME_PARAMETER));
      writeErrorResponse(response, exception, HttpStatus.UNAUTHORIZED);
    };
  }

  private LogoutSuccessHandler logoutSuccessHandler() {
    return new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT);
  }

  private AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, exception) -> {
      log.warn("unauthorized: {} {}", request.getMethod(), request.getRequestURI(), exception);
      writeErrorResponse(response, exception, HttpStatus.UNAUTHORIZED);
    };
  }

  private AccessDeniedHandler accessDeniedHandler() {
    return (request, response, exception) -> {
      log.warn("access denied: {} {}", request.getMethod(), request.getRequestURI(), exception);
      writeErrorResponse(response, exception, HttpStatus.FORBIDDEN);
    };
  }

  private void writeErrorResponse(HttpServletResponse response, Exception exception, HttpStatusCode status)
      throws IOException {
    if (response.isCommitted()) {
      return;
    }
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    var message = new Message(exception.getMessage());
    objectMapper.writeValue(response.getWriter(), new Body(message));
    response.flushBuffer();
  }

  private record Body(Message... messages) implements Serializable {

  }

  private record Message(String message) implements Serializable {

  }
}
