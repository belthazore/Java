package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/*@ComponentScan
@EnableAutoConfiguration*/
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        System.out.println("class Application started ...");
        SpringApplication.run(Application.class, args);
    }
}
