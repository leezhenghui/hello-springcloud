package hello.spring.cloud.calculator.ui.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import brave.Tracing;
import brave.opentracing.BraveTracer;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class AppConfig {

	private static final String SVC_NAME = "calculator-ui";
	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES =
			new HashSet<String>(Arrays.asList("application/json"));

	@LoadBalanced
	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public io.opentracing.Tracer zipkinTracer() {
		OkHttpSender okHttpSender = OkHttpSender.create("http://localhost:9411/api/v2/spans");

		AsyncReporter<Span> spanReporter = AsyncReporter.create(okHttpSender);

		Tracing braveTracing = Tracing.newBuilder()
				.localServiceName(SVC_NAME)
				.spanReporter(spanReporter)
				.build();
		return BraveTracer.create(braveTracing);
	}


	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("calculator-api-v1")
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.ant("/api/v1/**"))
				.build()
				.apiInfo(apiInfo())
				.consumes(DEFAULT_PRODUCES_AND_CONSUMES)
				.produces(DEFAULT_PRODUCES_AND_CONSUMES);
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Calculator Frond-End API Service",
				"Private API for calculagtor operations, so far only support add and subtract operations and for Spring-Cloud PoC Sample purpose only",
				"v1",
				"Terms of service",
				new Contact("Zhenghui Lee", "www.example.com", "leezhenghui@gmail.com"),
				"License of API", "API license URL", Collections.emptyList());
	}

}
