package ua.kpi.iasa.onlineradio.net;
import java.io.Serializable;

public class NetResponse implements Serializable {
    private boolean success;
    private Object data;
    private String message;

    public NetResponse(boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
    public String getMessage() { return message; }
}