package hello.spring.cloud.svc.ifw.runtime;

import hello.spring.cloud.svc.ifw.annotation.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public class Invocable {

    private static Logger logger = LoggerFactory.getLogger(Invocable.class);

    private static class Header extends Interceptor {
        public Header(int weight) {
            super(weight);
        }

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

        public void appendInterceptor(Interceptor i) {
            if (i == null || ! (i instanceof Invoker)) {
                return;
            }
            Invoker origInvoker = this.header.getNext();
            this.header.setNext(i);
            i.setNext(origInvoker);
        }

        public void process(RuntimeContext ctx) {
            this.header.invoke(ctx);
        }

    }

    private InvocationChain ic;
    private String operation;

    public Invocable(Method m, hello.spring.cloud.svc.ifw.annotation.Interceptor[] annotations) {

        try {
            Interceptor header = new Header(-1);
            Invoker tail = new Tail(m);
            this.ic = new InvocationChain(header, tail);
            ArrayList<Interceptor> il = new ArrayList<Interceptor>();
            for (hello.spring.cloud.svc.ifw.annotation.Interceptor annotation: annotations) {
                if (annotation.type() == null) {
                    continue;
                }
                Constructor c = annotation.type().getDeclaredConstructor(int.class);
                il.add((Interceptor) c.newInstance(annotation.weight()));
            }

            Collections.sort(il);
            for (Interceptor i: il) {
                ic.appendInterceptor(i);
            }

            this.operation = m.getDeclaringClass().getName() + "." + m.getName();
        } catch (InstantiationException e) {
           logger.error("Failed to create invocable, due to: ", e);
           throw new ServiceRuntimeException("Failed to create invocable", e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("Failed to create invocable, due to: ", e);
            throw new ServiceRuntimeException("Failed to create invocable", e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error("Failed to create invocable, due to: ", e);
            throw new ServiceRuntimeException("Failed to create invocable", e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error("Failed to create invocable, due to: ", e);
            throw new ServiceRuntimeException("Failed to create invocable", e.getMessage(), e);
        }
    }

    public Object invoke(Object target, Object[] inputs) {

        RuntimeContext rc = new RuntimeContext();
        rc.setInputs(inputs);
        rc.setTargetObject(target);
        rc.setOperation(this.operation);

        this.ic.process(rc);

        if (rc.getFault() != null) {
            throw rc.getFault();
        }

        return rc.getResult();
    }
}
