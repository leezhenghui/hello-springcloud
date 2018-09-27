package hello.spring.cloud.svc.ifw.runtime;

import hello.spring.cloud.svc.ifw.annotation.QoS;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeInvoker {

    private static Logger logger = LoggerFactory.getLogger(RuntimeInvoker.class);

    private ConcurrentHashMap<String, Invocable> cache = new ConcurrentHashMap<String, Invocable>();
    public static RuntimeInvoker INSTANCE = new RuntimeInvoker();

    private RuntimeInvoker() {
    }

    public Object invoke(ProceedingJoinPoint pjp) {
        Object reval = null;

        try {
            //Not support method override so far
            String key = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
            Invocable invocable = cache.get(key);
            if (invocable == null) {
                invocable = wrap(pjp);
                cache.put(key, invocable);
            }

            reval = invocable.invoke(pjp.getTarget(), pjp.getArgs());
        } catch (ServiceRuntimeException sre) {
            throw sre;
        } catch(Throwable err) {
            logger.error(err.getMessage(), err);
            ServiceRuntimeException sre = new ServiceRuntimeException("sys.ifw", err.getMessage(), err);
            throw sre;
        }

        return reval;
    }

    private Invocable wrap(ProceedingJoinPoint pjp) {

        Invocable invocable = null;
        try {
            Object target = pjp.getTarget();
            Class targetClass = AopProxyUtils.ultimateTargetClass(target);
            MethodSignature methodSig = (MethodSignature) pjp.getSignature();
            Method targetMethod = targetClass.getDeclaredMethod(methodSig.getName(), methodSig.getParameterTypes());
            QoS[] annotations = targetMethod.getAnnotationsByType(QoS.class);

            invocable = new Invocable(targetMethod, annotations[0].value());
        } catch (NoSuchMethodException e) {
            String reason = "Can not find method \"" + pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName() + "\"";
            logger.error(reason, e);
            throw new ServiceRuntimeException("ifw", reason, e);
        }

        return invocable;
    }


}
