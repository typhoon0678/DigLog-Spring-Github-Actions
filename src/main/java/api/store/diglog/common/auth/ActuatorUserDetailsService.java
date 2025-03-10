package api.store.diglog.common.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActuatorUserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Value("${actuator.username}")
    private String username;
    @Value("${actuator.password}")
    private String password;

    @Bean
    public UserDetailsService inMemoryActuatorUserDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

        userDetailsManager.createUser(
                User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .roles("ADMIN")
                        .build()
        );

        return userDetailsManager;
    }
}
