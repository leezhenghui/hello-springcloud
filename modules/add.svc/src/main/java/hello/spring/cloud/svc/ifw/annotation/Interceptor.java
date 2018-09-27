package hello.spring.cloud.svc.ifw.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
    int weight() default 100000;
    Class<? extends hello.spring.cloud.svc.ifw.runtime.Interceptor> type();
}
