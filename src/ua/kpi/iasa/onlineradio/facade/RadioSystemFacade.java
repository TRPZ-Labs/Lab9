package ua.kpi.iasa.onlineradio.facade;

import ua.kpi.iasa.onlineradio.models.IterationMode;
import ua.kpi.iasa.onlineradio.models.Track;
import ua.kpi.iasa.onlineradio.models.User;
import ua.kpi.iasa.onlineradio.net.NetCommand;
import ua.kpi.iasa.onlineradio.net.NetResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RadioSystemFacade {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;
    private User currentUser;

    public RadioSystemFacade() {
    }

    private NetResponse send(String command, Object... args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(new NetCommand(command, args));
            return (NetResponse) in.readObject();

        } catch (Exception e) {
            System.err.println("CLIENT Error: " + e.getMessage());
            return new NetResponse(false, null, "Connection Error");
        }
    }

    public boolean login(String username, String password) {
        NetResponse res = send("LOGIN", username, password);
        if (res.isSuccess()) {
            this.currentUser = (User) res.getData();
            return true;
        }
        return false;
    }

    public User getCurrentUser() { return currentUser; }

    public void play() { send("PLAY"); }
    public void nextTrack() { send("NEXT"); }

    public Track getCurrentTrack() {
        NetResponse res = send("GET_CURRENT");
        return (Track) res.getData();
    }

    public void likeCurrentTrack() { send("LIKE"); }

    public void changePlaybackMode(IterationMode mode) { send("MODE", mode); }

    public String generatePlaylistReport(int id) {
        return (String) send("REPORT").getData();
    }

    public String exportPlaylistToXml(int id) {
        return (String) send("XML").getData();
    }

    // Заглушка,
    public void setPlaylist(int id) { }
}