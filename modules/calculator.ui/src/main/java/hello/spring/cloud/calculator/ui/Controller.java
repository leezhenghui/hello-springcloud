package hello.spring.cloud.calculator.ui;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@org.springframework.web.bind.annotation.RestController
@EnableDiscoveryClient
@Api(value = "Calculator-UI(Frond-End API)")
public class Controller {

	private static Logger logger = LoggerFactory.getLogger(Controller.class);

	//@Autowired
	//private RestTemplate restTemplate;

//	@Autowired
//	private AddSvcClient ac;
//
//	@Autowired
//	private SubSvcClient sc;

	@Autowired
	private Calculator calculator;

	@GetMapping(value = "/throwErr")
	public void throwErr() {
		throw new UnsupportedOperationException("throw a unsupported operation exception");
	}

	@PostMapping(value = "/api/v1/compute", headers = {
			"content-type=application/json"
	})
	@ApiOperation(value = "Calculator Executor", response = Output.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully execution"),
			@ApiResponse(code = 401, message = "You are not authorized to execute"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 429, message = "Too Many Requests")
	})
	public Output compute(@RequestBody @ApiParam(required = true) Input input) {
		return calculator.execute(input);
	}

	@RequestMapping(value = "/api/v1/counter/config", method = RequestMethod.GET)
	@ApiOperation(value = "Counter Config", response = void.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully configured")
	})
	public void counterConfig(@RequestParam(value = "enabled") @ApiParam(required = true) boolean enabled) {
		logger.info("[counterConfig] [Enter]" + enabled);
		Calculator.counterEnabled = enabled;
		logger.info("[counterConfig] [Exit]");
	}
}