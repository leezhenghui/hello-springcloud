package hello.spring.cloud.svc.ifw.runtime.interceptor;

import hello.spring.cloud.svc.ifw.annotation.QoS;
import hello.spring.cloud.svc.ifw.runtime.RuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class Log extends hello.spring.cloud.svc.ifw.runtime.Interceptor{

    private static Logger logger = LoggerFactory.getLogger(Log.class);

    public Log(int weight) {
        super(weight);
    }

    private static class LogContext {
        private Date beginTime;
        private Date endTime;

        public String getIid() {
            return iid;
        }

        private String iid;

        public LogContext(String iid) {
            this.iid = iid;
        }

        public Date getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(Date beginTime) {
            this.beginTime = beginTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
    }

    @Override
    public void processRequest(RuntimeContext ctx) {
        Object lc = ctx.getContext(this.QName());

        if (lc == null) {
            lc = new LogContext(UUID.randomUUID().toString());
            ctx.putContext(this.QName(), lc);
        }
        ((LogContext)lc).setBeginTime(new Date());

        logger.info("[" + this.QName() +"] [ENTER] [Target]: " + ctx.getOperation() +
                    " [InvocationId]: " + ((LogContext)lc).getIid());
    }

    @Override
    public void processResponse(RuntimeContext ctx) {
        LogContext lc = (LogContext) ctx.getContext(this.QName());
        lc.setEndTime(new Date());

        logger.info("[" + this.QName() +"] [EXIT] [Target]: " + ctx.getOperation() +
                 " [InvocationId]: " + lc.getIid() +
                 " [Status]: SUCCEED " +
                 " [Duration]: " + (lc.getEndTime().getTime() - lc.getBeginTime().getTime()) + "ms");
    }

    @Override
    public void processFault(RuntimeContext ctx) {

        LogContext lc = (LogContext) ctx.getContext(this.QName());
        if (lc.getEndTime() == null) {
            lc.setEndTime(new Date());
        }

        logger.error("[" + this.QName() +"] [EXIT] [Target]: " + ctx.getOperation() +
                " [InvocationId]: " + lc.getIid() +
                " [Status]: FAILED " +
                " [Duration]: " + (lc.getEndTime().getTime() - lc.getBeginTime().getTime()) + "ms" +
                " [Details]: ", ctx.getFault().getDetails());
    }

    @Override
    public String QName() {
        return "SimpleLogger";
    }

    @Override
    public boolean accept(QoS[] annotation) {
        return true;
    }
}
