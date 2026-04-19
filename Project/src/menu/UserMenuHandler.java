package menu;

import exceptions.UserCreationException;
import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;
import document.service.DocumentService;
import manager.UserManager;
import model.*;

import java.io.*;
import java.util.*;

public class UserMenuHandler {
    private final UserManager userManager;

    private DocumentService documentService = new DocumentService();


    public UserMenuHandler(UserManager userManager)
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

            switch (user.getUserRole())
            {
                case ADMINISTRATOR:
                {
                    adminMenu(sc, out, user);
                    break;
                }
                case AUTHOR:
                {
                    authorMenu(sc, out, user);
                    break;
                }
                case REVIEWER:
                {
                    reviewerMenu(sc, out, user);
                    break;
                }
                case READER:
                {
                    readerMenu(sc, out, user);
                    break;
                }
            }
        }
    }

    private void readerMenu(Scanner sc, PrintStream out, User user) {
        out.println("Logged in as reader!");
    }

    private void reviewerMenu(Scanner sc, PrintStream out, User user) {
        out.println("Logged in as reviewer!");
    }






    private void adminMenu(Scanner sc, PrintStream out, User admin)
    {
        out.println("Logged in as admin.");
        out.println("Choose what action you want to take: " + Arrays.toString(userManager.getAdminActions().toArray()));



        if(sc.nextLine().equalsIgnoreCase("CREATE_USER")) {
            out.println("Enter user type to create: " + Arrays.toString(Role.values()) + ";");
            try {
                Role role = Role.valueOf(sc.nextLine().toUpperCase());

                out.println("Enter username:");
                String userName = sc.nextLine();

                out.println("Enter password:");
                String password = sc.nextLine();

                userManager.registerUser(userName, password, role);

                out.println("Success.");
            } catch (IllegalArgumentException e) {
                out.println("Error: Invalid user type.");
            } catch (UserCreationException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void authorMenu(Scanner sc, PrintStream out, User author) {
        out.println("Logged in as author.");
//        out.println("Choose what action you want to take: " + Arrays.toString(userManager.getAuthorActions().toArray()));
//
//        if(sc.nextLine().equalsIgnoreCase("L")) { //LIST_DOCUMENTS
//            out.print("Choose which of the following documents you want to view!");
//            try {
//                System.out.println(documentService.getDocuments());
//                List<Document> documentsList =  documentService.getDocuments();
//                //out.println(documentsList);
//                for(Document document : documentsList) {sype.");
//            }
//        }

        System.out.println("\nEntered as author!\n");
        while (true) {
            try {
                System.out.println("Waiting for request...");

                String request = readRequest(sc);

                if (request == null || request.isEmpty()) {
                    System.out.println("Client disconnected.");
                    break;
                }
                else if (request.equals("exit"))
                {
                    out.println("Goodbye!");
                    break;
                }

                String[] lines = request.split("\n");
                String command = lines[0];

                System.out.println("\nThe command is -> " + command + "\n");

                switch (command) {

                    case "CREATE_DOCUMENT" -> {
                        String title = lines[1];
                        String description = lines[2];
                        String documentType = lines[3];
                        int authorId = author.getUserId();
                        documentService.createDocument(title, description, authorId, DocumentType.valueOf(documentType));
                        sendResponse(out, "OK", "Document created!");
                    }

                    case "CREATE_VERSION" -> {
                        int docId = Integer.parseInt(lines[1]);
                        String content = lines[2];
                        int authorId = author.getUserId();

                        documentService.addVersionToDocument(docId, content, authorId);

                        sendResponse(out, "OK", "Version created!");
                    }

                    case "LIST_DOCUMENTS" -> {
                        List<Document> docs = documentService.getAllDocuments();

                        List<String> response = new ArrayList<>();
                        for (Document document : docs) {
                            response.add(document.getId() + " - " + document.getTitle());
                        }

                        sendResponse(out, response.toArray(new String[0]));
                    }


                    case "VIEW_VERSIONS" -> {
                        int docId = Integer.parseInt(lines[1]);

                        List<DocumentVersion> versions = documentService.getVersions(docId);

                        List<String> response = new ArrayList<>();

                        for (DocumentVersion v : versions) {
                            response.add("Version " + v.getVersionNumber() + " - " + v.getStatus());
                        }

                        sendResponse(out, response.toArray(new String[0]));
                    }

                    case "VIEW_DRAFT" -> {
                        int docId = Integer.parseInt(lines[1]);
                        int versionNumber = Integer.parseInt(lines[2]);

                        String content = documentService.getDraftContent(docId, versionNumber);

                        sendResponse(out, content);
                    }
                    case "EDIT_DRAFT" -> {
                        int docId = Integer.parseInt(lines[1]);
                        int versionNumber = Integer.parseInt(lines[2]);
                        String newContent = lines[3];
                        //documentService.editDraftDocument(docId, versionNumber, newContent);
                        sendResponse(out, "The draft is edited! A new version was created successfully!"); //add some other logic
                    }

                    case "LIST_DRAFTS" -> {
                        int docId = Integer.parseInt(lines[1]);
                        int versionNumber = Integer.parseInt(lines[2]);
                        //String content = documentService.getAllDrafts();
                        //sendResponse(out, content); //add some other logic
                    }

                    case "VIEW_DOCUMENT_HISTORY" -> {
                        int docId = Integer.parseInt(lines[1]);
                        //has to return all document info
                    }
                    case "REQUEST_DOCUMENT_TYPES" -> {
                        List<String> documentTypes = documentService.getDocumentTypes();
                        sendResponse(out, documentTypes.toArray(new String[0]));
                    }


                    case "EXIT" -> {
                        System.out.println("Client requested exit.");
                        out.println("You requested to exit. Goodbye!");
                        break;
                    }

                    default -> {
                        sendResponse(out, "ERROR", "Unknown command");
                    }
                }

            } catch (Exception e) {
                System.out.println("Error occurred: " + e.getMessage());
                break;
            }
        }
    }


    public static String readRequest(Scanner sc) {
        StringBuilder request = new StringBuilder();
        String line;

        while (!(line = sc.nextLine()).equals("END")) {
            request.append(line).append("\n");
        }

        return request.toString();
    }
    public static void sendResponse(PrintStream out, String... lines) {
        for (String line : lines) {
            out.println(line);
        }
        out.println("END");
    }


    /*public static void sendCommand(PrintStream out, String command) {
        out.println(command);
        out.println("END");
    }
    public static String readResponse(Scanner sc) {

        StringBuilder sb = new StringBuilder();
        String line;

        while (!(line = sc.nextLine()).equals("END")) {
            sb.append(line).append("\n");
        }

        return sb.toString();
    }*/


}
