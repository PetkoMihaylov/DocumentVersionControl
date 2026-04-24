package client.service;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static client.service.ClientUserMenuService.readResponse;
import static client.service.ClientUserMenuService.sendCommand;


public class AdminMenu {
    private static final Logger logger = Logger.getLogger(AdminMenu.class.getName());

    public static void showAdminMenu(Scanner console, Scanner sc, PrintStream out) {

        while (true) {
            System.out.println("""
                --- Admin Menu ---
                1. Create User
                2. List Users
                3. Change User Role
                4. List Documents
                0. Exit
                """);

            int choice;
            try {
                choice = Integer.parseInt(console.nextLine().trim());
            } catch (NumberFormatException ex) {
                logger.log(Level.WARNING, "Invalid number!");
                continue;
            }

            switch (choice) {


                case 1 -> { //CREATE_USER
                    System.out.print("What Role do you want the user to have? (ADMINISTRATOR/AUTHOR/REVIEWER/READER): "); //maybe get list from server?
                    String role = console.nextLine().trim();

                    System.out.print("Username: ");
                    String username = console.nextLine().trim();

                    System.out.print("Password: ");
                    String password = console.nextLine().trim();

                    sendCommand(out, "CREATE_USER", role, username, password);
                    System.out.println(readResponse(sc));
                }

                case 2 -> { //LIST_USERS
                    sendCommand(out, "LIST_USERS");
                    System.out.println(readResponse(sc));
                }

                case 3 -> { // CHANGE_ROLE
                    System.out.print("User ID to change: ");
                    String userId = console.nextLine().trim();

                    System.out.print("Choose a new role (ADMINISTRATOR/AUTHOR/REVIEWER/READER): ");
                    String newRole = console.nextLine().trim();

                    sendCommand(out, "CHANGE_ROLE", userId, newRole);
                    System.out.println(readResponse(sc));
                }


                case 4 -> { // LIST_DOCUMENTS
                    sendCommand(out, "LIST_DOCUMENTS");
                    System.out.println(readResponse(sc));
                }

                case 0 -> { // EXIT
                    sendCommand(out, "EXIT");
                    readResponse(sc);
                    System.out.println("Logged out from the administrator service!");
                    return;
                }

                default -> System.out.println("Invalid number! Please choose a number from the service!");
            }
        }
    }

}
