import org.bson.types.ObjectId;

import java.util.Date;

public class Event {

    private int reporterId;
    private Date timestamp;
    private int metricId;
    private int metricValue;
    private String message;

    Event(){}

    public Event(int reporterId, Date timestamp, int metricId, int metricValue, String message) {
        this.reporterId = reporterId;
        this.timestamp = timestamp;
        this.metricId = metricId;
        this.metricValue = metricValue;
        this.message = message;
    }


    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getMetricId() {
        return metricId;
    }

    public void setMetricId(int metricId) {
        this.metricId = metricId;
    }

    public int getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(int metricValue) {
        this.metricValue = metricValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "Event{" +
                ", reporterId=" + reporterId +
                ", timestamp=" + timestamp +
                ", metricId=" + metricId +
                ", metricValue=" + metricValue +
                ", message='" + message + '\'' +
                '}';
    }
}
