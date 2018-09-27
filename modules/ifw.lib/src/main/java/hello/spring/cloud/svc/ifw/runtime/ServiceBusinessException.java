package hello.spring.cloud.svc.ifw.runtime;

public class ServiceBusinessException extends Exception{
    private Throwable details;
    private String causedBy;
    private String reason;
    public ServiceBusinessException(String causedBy, String reason, Throwable details) {
        super(reason, details);
        this.causedBy = causedBy;
        this.reason = reason;
        this.details = details;
    }
}
