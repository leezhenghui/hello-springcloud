package hello.spring.cloud.svc.ifw.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum InteractionPhase {
    REQUEST,
    RESPONSE,
    FAULT
}

abstract class Interceptor implements Invoker {

    private static Logger logger = LoggerFactory.getLogger(Interceptor.class);

    private Invoker nexter;

    public abstract void processRequest(RuntimeContext ctx);
    public abstract void processResponse(RuntimeContext ctx);
    public abstract void processFault(RuntimeContext ctx);

    public void setNext(Invoker nexter) {
        this.nexter = nexter;
    }

    public Invoker getNext() {
        return this.nexter;
    }

    @Override
    public void invoke(RuntimeContext ctx) {
        InteractionPhase phase = InteractionPhase.REQUEST;
        try {
            processRequest(ctx);
            getNext().invoke(ctx);
            if (ctx.getFault() == null) {
                phase = InteractionPhase.RESPONSE;
                processResponse(ctx);
            } else {
                phase = InteractionPhase.FAULT;
                processFault(ctx);
            }
        } catch(ServiceRuntimeException sre) {
            logger.error(sre.getMessage(), sre.getDetails());
            if (phase != InteractionPhase.FAULT) {
                ctx.setFault(sre);
                processFault(ctx);
            }
        } catch(Throwable re) {
            logger.error(re.getMessage(), re);
            if (phase != InteractionPhase.FAULT) {
                ServiceRuntimeException sre = new ServiceRuntimeException(this.QName(), re.getMessage(), re);
                ctx.setFault(sre);
                processFault(ctx);
            }
        }
    }
}
