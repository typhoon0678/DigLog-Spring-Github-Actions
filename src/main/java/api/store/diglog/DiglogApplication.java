package api.store.diglog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class DiglogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiglogApplication.class, args);
    }

}
