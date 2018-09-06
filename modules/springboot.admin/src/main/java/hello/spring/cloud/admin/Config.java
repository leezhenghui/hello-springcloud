package hello.spring.cloud.admin;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import brave.Tracing;
import brave.opentracing.BraveTracer;

import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class Config {

	private static final String SVC_NAME = "springboot-admin";

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

}
