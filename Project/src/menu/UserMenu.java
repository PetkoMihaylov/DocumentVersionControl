package menu;

import customExceptions.CredentialsException;
import customExceptions.UserCreationException;
import document.model.Document;
import document.service.DocumentService;
import manager.UserManager;
import model.*;
import model.Reader;

import java.io.*;
import java.util.*;

public class UserMenu {
    private final UserManager userManager;

    private DocumentService documentService = new DocumentService();


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
        out.println("Logged in as reader!");
    }

    private void reviewerMenu(Scanner sc, PrintStream out, Reviewer user) {
        out.println("Logged in as reviewer!");
    }






    private void adminMenu(Scanner sc, PrintStream out, Administrator admin)
    {
        out.println("Logged in as admin.");
        out.println("Choose what action you want to take: " + Arrays.toString(userManager.getAdminActions().toArray()));



        if(sc.nextLine().equalsIgnoreCase("CREATEUSER")) {
            out.println("Enter user type to create: " + Arrays.toString(UserType.values()) + ";");
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
            } catch (UserCreationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void authorMenu(Scanner sc, PrintStream out, Author author) {
        out.println("Logged in as author!");
        out.println("Choose what action you want to take: " + Arrays.toString(userManager.getAuthorActions().toArray()));

        if((sc.nextLine().equalsIgnoreCase("LISTDOCUMENTS")) || (sc.nextLine().equalsIgnoreCase("L"))) {
            out.print("Choose which of the following documents you want to view!");
            try {
                System.out.println(documentService.getDocuments());
                List<Document> documentsList =  documentService.getDocuments();
                //out.println(documentsList);
                for(Document document : documentsList) {
                    System.out.println("This doc: " + document.getDocumentId() + " with " + document.getTitle());
                    System.out.println("Here are the versions -> " + document.getAllVersions());
                    document.printDocumentData();
                }


                out.println("Success.");
            } catch (IllegalArgumentException e) {
                out.println("Error: Invalid user type.");
            }
        }
    }


}
