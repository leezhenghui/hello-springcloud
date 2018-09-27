package hello.spring.cloud.svc.ifw.runtime;

import hello.spring.cloud.svc.ifw.annotation.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class Invocable {

    private static Logger logger = LoggerFactory.getLogger(Invocable.class);

    private static class Header extends Interceptor {
        @Override
        public void processRequest(RuntimeContext ctx) {
            //do nothing
        }

        @Override
        public void processResponse(RuntimeContext ctx) {
            // do nothing
        }

        @Override
        public void processFault(RuntimeContext ctx) {
            // do nothing
        }

        @Override
        public void invoke(RuntimeContext ctx) {
            getNext().invoke(ctx);
        }

        @Override
        public String QName() {
            return "sys.internal.header";
        }

        @Override
        public boolean accept(QoS[] annotation) {
            return true;
        }
    }

    private static class Tail implements Invoker {
        private Method m;
        public Tail(Method m) {
            this.m = m;
        }

        @Override
        public void invoke(RuntimeContext ctx) {
            try {
                Object reval = this.m.invoke(ctx.getTargetObject(), ctx.getInputs());
                ctx.setResult(reval);
            } catch(Throwable err) {
                logger.error(err.getMessage(), err);
                ServiceRuntimeException sre = new ServiceRuntimeException(this.QName(), err.getMessage(), err);
                ctx.setFault(sre);
            }
        }

        @Override
        public String QName() {
            return "sys.internal.tail";
        }

        @Override
        public boolean accept(QoS[] annotation) {
            return true;
        }
    }

    private static class InvocationChain {
        private Interceptor header;
        private Invoker tail;
        public InvocationChain(Interceptor header, Invoker tail) {
            this.header = header;
            this.tail = tail;
            header.setNext(tail);
        }

        public void process(RuntimeContext ctx) {
            this.header.invoke(ctx);
        }

    }

    private InvocationChain ic;

    public Invocable(Method m, hello.spring.cloud.svc.ifw.annotation.Interceptor[] annotations) {
        Interceptor header = new Header();
        Invoker tail = new Tail(m);

        this.ic = new InvocationChain(header, tail);
    }

    public Object invoke(Object target, Object[] inputs) {

        RuntimeContext rc = new RuntimeContext();
        rc.setInputs(inputs);
        rc.setTargetObject(target);

        this.ic.process(rc);

        if (rc.getFault() != null) {
            throw rc.getFault();
        }

        return rc.getResult();
    }
}
