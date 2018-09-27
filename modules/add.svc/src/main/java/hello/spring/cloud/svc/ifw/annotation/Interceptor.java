package hello.spring.cloud.svc.ifw.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
    int weight() default 100000;
    String QName() default "";
}
