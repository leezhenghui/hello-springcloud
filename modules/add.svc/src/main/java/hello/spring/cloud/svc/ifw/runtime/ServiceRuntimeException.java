package hello.spring.cloud.svc.ifw.runtime;

public class ServiceRuntimeException extends RuntimeException {

    private Throwable details;
    private String causedBy;

    public Throwable getDetails() {
        return details;
    }

    public String getCausedBy() {
        return causedBy;
    }

    public String getReason() {
        return reason;
    }

    private String reason;
    public ServiceRuntimeException(String causedBy, String reason, Throwable details) {
        super(reason, details);
        this.causedBy = causedBy;
        this.reason = reason;
        this.details = details;
    }
}
