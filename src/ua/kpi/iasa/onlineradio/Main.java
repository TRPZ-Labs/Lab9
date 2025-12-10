package ua.kpi.iasa.onlineradio;

import ua.kpi.iasa.onlineradio.facade.RadioSystemFacade;
import ua.kpi.iasa.onlineradio.ui.LoginForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        System.out.println("--- Online Radio Client Started ---");

        // Створюємо клієнтський фасад (він спробує підключитися до localhost:8888)
        RadioSystemFacade clientFacade = new RadioSystemFacade();

        SwingUtilities.invokeLater(() -> {
            new LoginForm(clientFacade).setVisible(true);
        });
    }
}