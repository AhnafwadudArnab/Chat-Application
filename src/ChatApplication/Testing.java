package ChatApplication;

import javax.swing.SwingUtilities;

public class Testing {
    public static void main(String[] args) throws InterruptedException {
        // Launch Server UI + start socket listener
        SwingUtilities.invokeLater(Server::new);
        Server.startServer();

        // Wait for server to be ready before client connects
        Thread.sleep(600);

        // Launch Client UI + connect to server
        SwingUtilities.invokeLater(Client::new);
        Client.startClient();
    }
}
