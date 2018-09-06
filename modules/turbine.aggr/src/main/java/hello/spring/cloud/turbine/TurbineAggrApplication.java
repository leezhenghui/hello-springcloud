package hello.spring.cloud.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableDiscoveryClient
@EnableHystrixDashboard
@EnableTurbine
@SpringBootApplication
public class TurbineAggrApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbineAggrApplication.class, args);
	}
}
