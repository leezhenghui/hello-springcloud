package hello.spring.cloud.svc.ifw.wrap;

import hello.spring.cloud.svc.ifw.runtime.RuntimeInvoker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class QoSWrapper {

    private static Logger logger = LoggerFactory.getLogger(QoSWrapper.class);

    @Pointcut("@annotation(hello.spring.cloud.svc.ifw.annotation.QoS)")
    public void QoSConernedCutPoint() {
    }

    @Around("QoSConernedCutPoint()")
    public Object QoSWrap(ProceedingJoinPoint pjp) throws Throwable{
        Object reval = RuntimeInvoker.INSTANCE.invoke(pjp);
        return reval;
    }
}
