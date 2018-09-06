package hello.spring.cloud.calculator.ui;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Output {
    @ApiModelProperty(required = true)
    public int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
