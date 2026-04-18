package server;

import document.service.DocumentManager;
import manager.UserManager;
import menu.UserMenuHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLClientInfoException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private UserManager userManager;
    private UserMenuHandler userMenuHandler;
    private DocumentManager documentManager;
    private ServerSocket serverSocket;

    private static final Logger logger = Logger.getLogger(Server.class.getName());


    public Server()
    {
        userManager = new UserManager();
        userMenuHandler = new UserMenuHandler(userManager);
        documentManager = new DocumentManager();
    }


    public void start() {

        startServerSocket();
        startClientThread();

    }

    private void startServerSocket() {
        System.out.println("The server is listening.");
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        }
    }

    public void startClientThread() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Accepted client.");

                Thread clientThread = new Thread(() -> handleClient(client));
                clientThread.start();

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error occurred on Client thread. Disconnected probably.", e);
            }
        }
    }

    private void handleClient(Socket client) {
        try (
                Socket c = client;
                Scanner sc = new Scanner(c.getInputStream());
                PrintStream out = new PrintStream(c.getOutputStream())
        ) {
            userMenuHandler.startMenu(sc, out);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        }
    }
}