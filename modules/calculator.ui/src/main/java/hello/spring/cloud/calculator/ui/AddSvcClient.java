package hello.spring.cloud.calculator.ui;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AddSvcClient {

    private final RestTemplate restTemplate;
    private final String SVC_NAME = "add-svc";

    @Autowired
    public AddSvcClient(RestTemplate rt) {
        this.restTemplate = rt;
    }

    @HystrixCommand(fallbackMethod = "onFailure", commandKey = "add-cmd",commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "1000"),
            @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests", value = "1000"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value="60"),
            @HystrixProperty(name = "execution.timeout.enabled", value = "false")
    })
    public int add(int l, int r) {
        try {
            int result = this.restTemplate.getForObject("http://" + SVC_NAME+ "/api/v1/execute/?l={l}&r={r}", Integer.class, l, r);
            return result;
        } catch (Throwable e) {
            // e.printStackTrace();
            throw e;
        }
    }

    public int onFailure(int l, int r) {
        System.err.println("[ERROR]: Failed to call remote add-svc due to unavialable service");
        return -99999999;
    }
}
