package at.operuit.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class, R2dbcAutoConfiguration.class})
public class OperuitBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperuitBackendApplication.class, args);
        
        try {
            new OperuitMain().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
