package hello.spring.cloud.svc.ifw.runtime;

import hello.spring.cloud.svc.ifw.annotation.QoS;

public interface Invoker {

    public void invoke(RuntimeContext ctx);

    public String QName();

    public boolean accept(QoS[] annotation);
}
