package hello.spring.cloud.svc.add;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AddSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddSvcApplication.class, args);
	}
}
