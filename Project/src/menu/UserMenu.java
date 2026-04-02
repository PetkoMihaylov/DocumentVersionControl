package menu;

import customExceptions.CredentialsException;
import manager.UserManager;
import model.*;
import model.Reader;

import java.io.*;
import java.util.*;

public class UserMenu {
    private final UserManager userManager;

    public UserMenu(UserManager userManager)
    {
        this.userManager = userManager;
    }



    public void startMenu(Scanner sc, PrintStream out) {
        showUserMenu(sc, out);
    }

    private void showUserMenu(Scanner sc, PrintStream out){
        while (true)
        {
            out.println("Login? Y/N");
            String login = sc.nextLine();

            if (!login.equalsIgnoreCase("Y"))
            {
                out.println("Goodbye.");
                return;
            }

            out.println("Enter username:");
            String userName = sc.nextLine();

            out.println("Enter password:");
            String password = sc.nextLine();

            User user = userManager.login(userName, password);

            if (user == null)
            {
                out.println("Error: Invalid login.");
                continue;
            }

            switch (user.getUserType())
            {
                case ADMINISTRATOR:
                {
                    adminMenu(sc, out, (Administrator) user);
                    break;
                }
                case AUTHOR:
                {
                    authorMenu(sc, out, (Author) user);
                    break;
                }
                case REVIEWER:
                {
                    reviewerMenu(sc, out, (Reviewer) user);
                    break;
                }
                case READER:
                {
                    readerMenu(sc, out, (model.Reader) user);
                    break;
                }
            }
        }
    }

    private void readerMenu(Scanner sc, PrintStream out, Reader user) {
    }

    private void reviewerMenu(Scanner sc, PrintStream out, Reviewer user) {
    }

    private void authorMenu(Scanner sc, PrintStream out, Author user) {
    }




    private void adminMenu(Scanner sc, PrintStream out, Administrator admin)
    {
        out.println("Logged in as admin.");
        out.println("Choose what action you want to take: " + Arrays.toString(userManager.getAdminActions().toArray()));



        if(sc.nextLine().equalsIgnoreCase("CREATEUSER")) {
            out.println("Enter user type to create: (ADMINISTRATOR | AUTHOR | REVIEWER | READER) ;");
            try {
                UserType userType = UserType.valueOf(sc.nextLine().toUpperCase());

                out.println("Enter username:");
                String userName = sc.nextLine();

                out.println("Enter password:");
                String password = sc.nextLine();

                userManager.registerUser(userName, password, userType);

                out.println("Success.");
            } catch (IllegalArgumentException e) {
                out.println("Error: Invalid user type.");
            } catch (CredentialsException e) {
                out.println(e.getMessage());
            }
        }
    }



}
