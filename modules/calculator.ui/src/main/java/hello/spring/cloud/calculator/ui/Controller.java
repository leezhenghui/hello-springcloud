package hello.spring.cloud.calculator.ui;

import io.swagger.annotations.*;
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

	//@Autowired
	//private RestTemplate restTemplate;

	@Autowired
	private AddSvcClient ac;

	@Autowired
	private SubSvcClient sc;

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
		Output reval = new Output();
		String exp = input.getExpression();
		String[] nums = exp.split("[+|-]");
		ArrayList<Integer> normalizedNums = new ArrayList<Integer>();
		for (int i=0; i < nums.length; i++) {
			if (isWhiteSpaceOrEmptyString(nums[i])) {
				continue;
			}

			Integer num = Integer.parseInt(nums[i]);
			normalizedNums.add(num);
		}

		String[] ops = exp.split("[0-9]*");
		ArrayList<String> normalizedOps = new ArrayList<String>();
		for (int i=0; i < ops.length; i++) {
			if (isWhiteSpaceOrEmptyString(ops[i])) {
				continue;
			}

			normalizedOps.add(ops[i].trim());
		}

		if (normalizedNums.size() <= 1) {
			reval.setResult(normalizedNums.get(0));
			return reval;
		}

		int result = 0;
		for (int i = 0; i < normalizedOps.size(); i++) {
			int l = result;
			if ( i == 0) {
				l = normalizedNums.get(i);
			}
			String op = normalizedOps.get(i);
			int r = normalizedNums.get(i + 1);
			result = callRemoteOperator(l, r, op);
			if (result == -99999999) {
				reval.setResult(result);
				return reval;
			}
		}

		System.out.println("[DEBUG]: " + dumpExp(normalizedNums, normalizedOps) + "= " + result);
		reval.setResult(result);
		return reval;
	}

	private int callRemoteOperator(int l, int r, String op) {
		if (op.equals("+")) {
			return ac.add(l, r);
		}
		return sc.subtract(l,r);
	}

	private boolean isWhiteSpaceOrEmptyString(String str) {
		if (str == null || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	private String dumpExp(ArrayList<Integer> nums, ArrayList<String> ops) {
		String reval = "";

		for (int i=0; i < nums.size(); i++) {
			reval += " " + nums.get(i) + " ";
			if (i < ops.size()) {
				reval += ops.get(i);
			}
		}
		return reval;
	}
}