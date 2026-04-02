package Server;

import manager.UserManager;
import menu.UserMenu;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private UserManager userManager;
    private UserMenu userMenu;
    private ServerSocket serverSocket;

    private static final Logger logger = Logger.getLogger(Server.class.getName());


    public Server()
    {
        userManager = new UserManager();
        userMenu = new UserMenu(userManager);
    }


    public void start() {
            startServerSocket();
            startClientThread();


        // For offline testing
        //userMenu(new Scanner(System.in), System.out);
    }


    private ServerSocket startServerSocket(){
        System.out.println("The server is listening.");
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        }
        return serverSocket;
    }

    public void startClientThread() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Accepted client.");
                Scanner sc = null;
                PrintStream out = null;

                Thread clientThread = new Thread(() -> {
                    startMenu(sc, out, client);
                });

                clientThread.start();

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error occurred", e);
            }
        }
    }


    private void startMenu(Scanner sc, PrintStream out, Socket client) {
        try {
            sc = new Scanner(client.getInputStream());
            out = new PrintStream(client.getOutputStream());
            //UserMenu userMenu = new UserMenu();
            userMenu.startMenu(sc, out);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        } finally {
            if (sc != null)
                sc.close();
            if (out != null)
                out.close();
        }
    }
}