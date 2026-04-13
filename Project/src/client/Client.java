package client;


import client.ui.LanternaEditor;
import document.service.DocumentService;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.menu.UserMenu.showUserMenu;


public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {

        try (Socket server = new Socket("localhost", 8080);
            Scanner console = new Scanner(System.in);
            Scanner sc = new Scanner(server.getInputStream())) {
                PrintStream out = new PrintStream(server.getOutputStream());

                showUserMenu(console, sc, out);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error occurred", e);
            }
    }
}
