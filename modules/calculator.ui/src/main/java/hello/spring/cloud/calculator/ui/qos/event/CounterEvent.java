package hello.spring.cloud.calculator.ui.qos.event;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class CounterEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getUuid() {
        return uuid;
    }

    private String uuid;

    public CounterEvent() {
        this.uuid = UUID.randomUUID().toString();
    }

    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("UUID:").append(this.uuid).append("; count:").append(this.count).append("; timestamp:").append(this.timestamp.toString());

        return sb.toString();
    }

}
