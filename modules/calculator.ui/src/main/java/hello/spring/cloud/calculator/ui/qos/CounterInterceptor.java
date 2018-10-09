package hello.spring.cloud.calculator.ui.qos;

import hello.spring.cloud.calculator.ui.ContextProvider;
import hello.spring.cloud.calculator.ui.qos.event.CounterEvent;
import hello.spring.cloud.svc.ifw.annotation.QoS;
import hello.spring.cloud.svc.ifw.runtime.Interceptor;
import hello.spring.cloud.svc.ifw.runtime.RuntimeContext;
import hello.spring.cloud.svc.ifw.runtime.ServiceRuntimeException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CounterInterceptor extends Interceptor{

    public static interface TxAwaredAction {
        public void run() throws ServiceRuntimeException;
    }

    private KafkaTemplate kt;
    private String topic;

    public CounterInterceptor(int weight, Properties conf) {
        super(weight, conf);
        this.kt = (KafkaTemplate) ContextProvider.getBean("kafkaTemplate");
        this.topic = conf.getProperty("topic");
    }

    @Override
    public void processRequest(RuntimeContext ctx) {

        CounterEvent ce = new CounterEvent();
        ce.setCount(1);
        ce.setTimestamp(new Date());

        try {
            this.kt.send(this.topic, ce).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException err) {
            throw new ServiceRuntimeException(this.QName(), "ExecutionException", err);
        } catch (TimeoutException | InterruptedException err) {
            throw new ServiceRuntimeException(this.QName(), "TimeoutException", err);
        }
    }

    @Override
    public void invoke(RuntimeContext ctx) {
//        TxAwaredAction txAwaredAction = new TxAwaredAction() {
//            @Transactional(transactionManager = "chainedTxMgrt")
//            public void run() throws ServiceRuntimeException{
//                CounterInterceptor.super.invoke(ctx);
//                if (ctx.getFault() != null) {
//                    // rollback
//                    throw ctx.getFault();
//                }
//            }
//        };
//
//        try {
//            txAwaredAction.run();
//        } catch (ServiceRuntimeException ser) {
//            // do nothing
//        }

        this.kt.executeInTransaction(new KafkaOperations.OperationsCallback() {
            @Override
            public Object doInOperations(KafkaOperations kafkaOperations) {
                CounterInterceptor.super.invoke(ctx);
                if (ctx.getFault() != null) {
                    throw ctx.getFault();
                }
                return true;
            }
        });
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
    public String QName() {
        return "Invocation.Counter";
    }

    @Override
    public boolean accept(QoS[] annotation) {
        return true;
    }
}
