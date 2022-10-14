package com.pspatel.CRUDService.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig {

  private static final String[] AUTH_WHITELIST = {
      // -- Swagger UI v2
      "/v2/api-docs",
      "/swagger-resources",
      "/swagger-resources/**",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui.html",
      "/webjars/**",
      // -- Swagger UI v3 (OpenAPI)
      "/v3/api-docs/**",
      "/swagger-ui/**"
  };

  private final RsaKeyProperties rsaKeys;

  public SecurityConfig(RsaKeyProperties rsaKeys) {
    this.rsaKeys = rsaKeys;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .authorizeRequests(auth -> {
          auth.antMatchers(AUTH_WHITELIST).permitAll();
        	auth.anyRequest().authenticated();
        
        	})
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  @Bean
  public InMemoryUserDetailsManager user() {
    UserDetails user =
        User.withUsername("pspatel").password("{noop}password").authorities("read").build();

    UserDetails admin =
        User.withUsername("admin").password("{noop}password").authorities("read").build();

    return new InMemoryUserDetailsManager(user, admin);
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
  }

  @Bean
  JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwks);
  }
}
