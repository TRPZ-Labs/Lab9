package ua.kpi.iasa.onlineradio.net;
import java.io.Serializable;

public class NetCommand implements Serializable {
    private String command;
    private Object[] args;

    public NetCommand(String command, Object... args) {
        this.command = command;
        this.args = args;
    }
    public String getCommand() { return command; }
    public Object[] getArgs() { return args; }
}