package ua.kpi.iasa.onlineradio.server;

import ua.kpi.iasa.onlineradio.data.*;
import ua.kpi.iasa.onlineradio.models.*;
import ua.kpi.iasa.onlineradio.models.visitor.ReportVisitor;
import ua.kpi.iasa.onlineradio.models.visitor.XmlExportVisitor;
import ua.kpi.iasa.onlineradio.repositories.*;
import java.util.Optional;

public class RadioService {
    private final IUserRepository userRepo;
    private final ITrackRepository trackRepo;
    private final IPlaylistRepository playlistRepo;
    private final IPlaybackEventRepository historyRepo;
    private final MusicLibrary library;
    private final Streamer streamer;
    private User currentUser;

    public RadioService() {
        this.trackRepo = new TrackRepository();
        this.userRepo = new UserRepository();
        this.playlistRepo = new PlaylistRepository();
        this.historyRepo = new PlaybackEventRepository();
        this.library = new MusicLibrary(trackRepo);
        this.streamer = new Streamer();
        setupInitialData();
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPasswordHash().equals(password)) {
            this.currentUser = userOpt.get();
            System.out.println("SERVER: User logged in: " + username);
            return true;
        }
        return false;
    }

    public User getCurrentUser() { return currentUser; }

    public void play() {
        streamer.play();
        logHistory();
    }

    public void nextTrack() {
        streamer.nextTrack();
        logHistory();
    }

    public Track getCurrentTrack() {
        return streamer.getCurrentTrack();
    }

    public void setPlaylist(int id) {
        playlistRepo.findById(id).ifPresent(streamer::setActivePlaylist);
    }

    public void changeMode(IterationMode mode) {
        playlistRepo.findById(1).ifPresent(p -> {
            p.setMode(mode);
            streamer.setActivePlaylist(p);
        });
    }

    public void likeCurrent() {
        Track t = streamer.getCurrentTrack();
        if(t != null && currentUser != null) t.addLike(currentUser);
    }

    public String getReport(int id) {
        Optional<Playlist> p = playlistRepo.findById(id);
        if(p.isPresent()) {
            ReportVisitor v = new ReportVisitor();
            p.get().accept(v);
            return v.getReport();
        }
        return "Not found";
    }

    public String getXml(int id) {
        Optional<Playlist> p = playlistRepo.findById(id);
        if(p.isPresent()) {
            XmlExportVisitor v = new XmlExportVisitor();
            p.get().accept(v);
            v.closePlaylist();
            return v.getXml();
        }
        return "Error";
    }

    private void logHistory() {
        if (streamer.getCurrentTrack() != null && currentUser != null) {
            historyRepo.save(new PlaybackEvent(currentUser.getId(), streamer.getCurrentTrack().getId()));
        }
    }

    private void setupInitialData() {

        userRepo.save(new Administrator(0, "admin", "admin"));
        userRepo.save(new User(0, "listener", "1234"));
        Track t1 = new Track(0, "Bohemian Rhapsody", "Queen", "music/queen.mp3");
        Track t2 = new Track(0, "Smells Like Teen Spirit", "Nirvana", "music/nirvana.mp4");
        trackRepo.save(t1); trackRepo.save(t2);
        Playlist p1 = new Playlist(0, "Best Rock");
        p1.addTrack(t1); p1.addTrack(t2);
        playlistRepo.save(p1);
    }
}