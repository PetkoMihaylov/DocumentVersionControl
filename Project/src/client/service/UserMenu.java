package client.service;

import java.io.PrintStream;
import java.util.Scanner;

import static client.service.AdminMenu.showAdminMenu;
import static client.service.AuthorMenu.showAuthorMenu;
import static client.service.ReaderMenu.showReaderMenu;
import static client.service.ReviewerMenu.showReviewerMenu;


public class UserMenu {

    public static void showUserMenu(Scanner console, Scanner sc, PrintStream out) {
        while (true) {
            // Login message
            System.out.println(sc.nextLine());

            // Enter Y/N for login
            out.println(console.nextLine());

            String next = sc.nextLine();
            System.out.println(next);
            if (next.equals("Goodbye.")) {
                return;
            }
            // Enter username
            out.println(console.nextLine());

            // Enter password
            System.out.println(sc.nextLine());
            out.println(console.nextLine());

            // Login type
            next = sc.nextLine();
            System.out.println(next);
            if (next.startsWith("Error")) {
                continue;
            }
            if (next.equals("Logged in as admin.")) {
                showAdminMenu(console, sc, out);
            }
            else if (next.equals("Logged in as author.")) {
                showAuthorMenu(console, sc, out);
            }
            else if (next.equals("Logged in as reviewer.")) {
                showReviewerMenu(console, sc, out);
            }
            else if (next.equals("Logged in as reader.")) {
                showReaderMenu(console, sc, out);
            }
        }
    }

}
