package poc.restToSoap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan("poc.restToSoap")
public class RestToSoapApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestToSoapApplication.class, args);
    }

}
