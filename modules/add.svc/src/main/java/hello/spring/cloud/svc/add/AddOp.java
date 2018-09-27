package hello.spring.cloud.svc.add;

import hello.spring.cloud.svc.ifw.annotation.Interceptor;
import hello.spring.cloud.svc.ifw.annotation.QoS;
import org.springframework.stereotype.Service;

@Service
public class AddOp {

    @QoS(value = {
            @Interceptor(weight = 1, QName = "SimpleLogger")
    })
    public int action(int l, int r) {
        int result = l + r;
        System.out.println("[DEBUG] " + l + " + " + r + " = " + result);
        // Thread.dumpStack();
        return result;
    }
}
