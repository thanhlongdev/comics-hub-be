package vn.longvo.comicshubbe.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.ui.ModelMap;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import vn.longvo.comicshubbe.common.constant.ErrorCodeEnum;
import vn.longvo.comicshubbe.common.http.BaseResponse;
import vn.longvo.comicshubbe.common.http.FailureResponse;
import vn.longvo.comicshubbe.common.properties.AuthenticationProperties;
import vn.longvo.comicshubbe.common.properties.CORSProperties;
import vn.longvo.comicshubbe.common.properties.CRFSProperties;
import vn.longvo.comicshubbe.common.properties.SessionProperties;

@Log4j2
@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final SessionProperties sessionProperties;
  private final CRFSProperties crfsProperties;
  private final CORSProperties corsProperties;
  private final AuthenticationProperties authenticationProperties;

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.debug(true);
  }

  @Bean
  public SecurityFilterChain buildSecurityFilterChain(HttpSecurity http) throws Exception {
    applySessionConfig(http);
    applyCRFSConfiguration(http);
    applyCORSConfiguration(http);
    applyAuthenticationConfig(http);
    applyExceptionHandler(http);
    return http.build();
  }

  private void applySessionConfig(HttpSecurity http) throws Exception {
    http.sessionManagement((config) -> {
      config.sessionCreationPolicy(sessionProperties.getPolicy());
    });
  }

  private void applyCRFSConfiguration(HttpSecurity http) throws Exception {
    if (!crfsProperties.isEnabled()) {
      http.csrf(AbstractHttpConfigurer::disable);
    }
  }

  private void applyCORSConfiguration(HttpSecurity http) throws Exception {
    if (corsProperties.isEnabled()) {
      http.cors((config) -> {
        config.configurationSource(this.buildCORSConfiguration());
      });
    } else {
      log.debug("CORS is disabled. Allowed all request from all origins");
    }
  }

  private void applyAuthenticationConfig(HttpSecurity http) throws Exception {
    if (authenticationProperties.isByPassAuth()) {
      log.warn("*** WARNING: Authentication was bypassed. Do NOT use in production deployments.");
      http.authorizeRequests(config -> {
        config.anyRequest().permitAll();
      });
      return;
    }

    http.authorizeRequests(config -> {
      if (!authenticationProperties.getPublicPaths().isEmpty()) {
        authenticationProperties.getPublicPaths().forEach(publicPath -> {
          if (publicPath.getMethods().isEmpty()) {
            config.antMatchers(publicPath.getUrl()).permitAll();
          } else {
            publicPath.getMethods().forEach(method -> {
              config.antMatchers(method, publicPath.getUrl()).permitAll();
            });
          }
        });
      }
      config.anyRequest().authenticated();
    });
  }

  private void applyExceptionHandler(HttpSecurity http) throws Exception {
    http.exceptionHandling(config -> {
      config.authenticationEntryPoint(buildAuthenticationEntryPoint());
      config.accessDeniedHandler(buildAccessDeniedHandler());
    });
  }

  private CorsConfigurationSource buildCORSConfiguration() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setMaxAge(corsProperties.getMaxAge());
    if (!corsProperties.getAllowedOrigins().isEmpty()) {
      corsProperties.getAllowedOrigins().forEach(configuration::addAllowedOrigin);
    }
    if (!corsProperties.getAllowedMethods().isEmpty()) {
      corsProperties.getAllowedMethods().forEach(configuration::addAllowedMethod);
    }
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private AuthenticationEntryPoint buildAuthenticationEntryPoint() {
    return ((request, response, authException) -> {
      BaseResponse responseEntity = new FailureResponse()
          .setErrorMessage("You need to login first in order to perform this action.")
          .setErrorCode(ErrorCodeEnum.NEED_AUTHENTICATE);
      ResponseEntity<?> build = responseEntity.build();

      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write(new ObjectMapper().writeValueAsString(build.getBody()));
    });
  }

  private AccessDeniedHandler buildAccessDeniedHandler() {
    return ((request, response, accessDeniedException) -> {
      BaseResponse responseEntity = new FailureResponse()
          .setErrorMessage("You don't have required role to perform this action.")
          .setErrorCode(ErrorCodeEnum.NOT_HAVE_PERMISSION);
      ResponseEntity<?> build = responseEntity.build();

      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.getWriter().write(new ObjectMapper().writeValueAsString(build.getBody()));
    });
  }

}
