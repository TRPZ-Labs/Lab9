package ua.kpi.iasa.onlineradio.server;

import ua.kpi.iasa.onlineradio.models.IterationMode;
import ua.kpi.iasa.onlineradio.net.NetCommand;
import ua.kpi.iasa.onlineradio.net.NetResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 8888;
    private static RadioService service;

    public static void main(String[] args) {
        service = new RadioService();
        service.setPlaylist(1);

        System.out.println("SERVER: Started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            NetCommand cmd = (NetCommand) in.readObject();
            System.out.println("SERVER: Received command " + cmd.getCommand());

            NetResponse response = processCommand(cmd);
            out.writeObject(response);

        } catch (Exception e) {
            System.err.println("SERVER Error: " + e.getMessage());
        }
    }

    private static NetResponse processCommand(NetCommand cmd) {
        try {
            switch (cmd.getCommand()) {
                case "LOGIN":
                    boolean res = service.login((String)cmd.getArgs()[0], (String)cmd.getArgs()[1]);
                    return new NetResponse(res, service.getCurrentUser(), res ? "OK" : "Fail");
                case "PLAY":
                    service.play();
                    return new NetResponse(true, null, "Playing");
                case "NEXT":
                    service.nextTrack();
                    return new NetResponse(true, null, "Next");
                case "GET_CURRENT":
                    return new NetResponse(true, service.getCurrentTrack(), "OK");
                case "LIKE":
                    service.likeCurrent();
                    return new NetResponse(true, null, "Liked");
                case "MODE":
                    service.changeMode((IterationMode) cmd.getArgs()[0]);
                    return new NetResponse(true, null, "Mode changed");
                case "REPORT":
                    return new NetResponse(true, service.getReport(1), "Report");
                case "XML":
                    return new NetResponse(true, service.getXml(1), "XML");
                default:
                    return new NetResponse(false, null, "Unknown command");
            }
        } catch (Exception e) {
            return new NetResponse(false, null, e.getMessage());
        }
    }
}