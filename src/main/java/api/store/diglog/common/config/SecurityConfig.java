package api.store.diglog.common.config;

import api.store.diglog.common.auth.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final JWTUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final UserDetailsService inMemoryActuatorUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] oauth2Api = {"/oauth2/**"};
        String[] memberApi = {"/api/member/login", "/api/member/logout", "/api/member/refresh", "/api/member/profile/*", "/api/member/profile/search/*", "/api/verify/**"};
        String[] postGetApi = {"/api/post", "/api/post/*", "/api/post/member/tag"};
        String[] commentGetApi = {"/api/comment"};
        String[] folderGetApi = {"/api/folders/**"};
        String[] tagGetApi = {"/api/tag/**"};

        http
                .securityMatcher("/api/**", "/oauth2/**")
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(oauth2Api).permitAll()
                        .requestMatchers(memberApi).permitAll()
                        .requestMatchers(HttpMethod.GET, postGetApi).permitAll()
                        .requestMatchers(HttpMethod.GET, commentGetApi).permitAll()
                        .requestMatchers(HttpMethod.GET, folderGetApi).permitAll()
                        .requestMatchers(HttpMethod.GET, tagGetApi).permitAll()
                        .requestMatchers("/api/**").authenticated())

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .oauth2Login(oauth2 -> oauth2
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/api/login/oauth2/code/*"))
                        .userInfoEndpoint(userinfo -> userinfo
                                .userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler))

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }


    @Bean
    public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
        String[] apis = {"/swagger-ui/**", "/bus/v3/api-docs/**", "/v3/api-docs/**", "/actuator/**"};

        http
                .securityMatcher(apis)
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(apis).hasRole("ADMIN")
                        .anyRequest().permitAll())

                .httpBasic(Customizer.withDefaults())

                .userDetailsService(inMemoryActuatorUserDetailsService)

                .build();

        return http.getOrBuild();
    }
}
