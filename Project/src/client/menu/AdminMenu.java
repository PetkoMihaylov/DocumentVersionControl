package client.menu;

import java.io.PrintStream;
import java.util.Scanner;

public class AdminMenu {
    public static void showAdminMenu(Scanner console, Scanner sc, PrintStream out)
    {
        // UserType
        System.out.println(sc.nextLine());
        out.println(console.nextLine());

        // Username
        String next = sc.nextLine();
        System.out.println(next);
        if (next.startsWith("Error")) {
            return;
        }
        out.println(console.nextLine());

        // Password
        next = sc.nextLine();
        System.out.println(next);
        if (next.startsWith("Error")) {
            return;
        }
        out.println(console.nextLine());

        // result
        System.out.println(sc.nextLine());
    }
}
