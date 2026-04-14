package client.menu;

import java.io.PrintStream;
import java.util.Scanner;

public class ClientUserMenuService {


    public static void sendCommand(PrintStream out, String... lines) {
        for (String line : lines) {
            out.println(line);
        }
        out.println("END");
    }

    public static String readResponse(Scanner sc) {
        StringBuilder response = new StringBuilder();
        String line;

        while (!(line = sc.nextLine()).equals("END")) {
            response.append(line).append("\n");
        }

        return response.toString();
    }
}
