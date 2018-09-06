package hello.spring.cloud.calculator.ui;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Input {
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @ApiModelProperty(required = true, example = "1+2-1-2+3", notes = "Only support \"+\" and \"-\" operators for now")
    public String expression;


}
