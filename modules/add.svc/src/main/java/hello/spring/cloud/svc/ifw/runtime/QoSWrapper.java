package hello.spring.cloud.svc.ifw.runtime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class QoSWrapper {

    @Pointcut("@annotation(hello.spring.cloud.svc.ifw.annotation.QoS)")
    public void QoSConernedCutPoint() {
    }

    @Around("QoSConernedCutPoint()")
    public Object QoSWrap(ProceedingJoinPoint pjp) throws Throwable{
//        System.out.println("[Pre-Hook] Enter");
//        System.out.println("[Pre-Hook] Exit");
        Object reval = pjp.proceed();
//        System.out.println("[Post-Hook] Enter");
//        System.out.println("[Post-Hook] Exit");
        return reval;
    }
}
