package client.service;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientUserMenuService {

    private static final Logger logger = Logger.getLogger(ClientUserMenuService.class.getName());

    public static void sendCommand(PrintStream out, String... lines) {
        for (String line : lines) {
            out.println(line);
        }
        out.println("END");
    }

    public static String readResponse(Scanner sc) {
        StringBuilder response = new StringBuilder();
        String line;
        try {
            while (!(line = sc.nextLine()).equals("END")) {
                response.append(line).append("\n");
            }
        }catch (NoSuchElementException e) {
            logger.log(Level.WARNING, "Connection interrupted: Server unavailable!");
        }
        return response.toString();
    }
}
