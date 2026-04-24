package menu;

import exceptions.DocumentCreationException;
import exceptions.UserCreationException;
import model.Document;
import model.DocumentType;
import model.DocumentVersion;
import service.DocumentService;
import service.UserManager;
import model.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserMenuHandler {
    private final UserManager userManager;

    private DocumentService documentService = new DocumentService();
    private static final Logger logger = Logger.getLogger(UserMenuHandler.class.getName());


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
            String login = "";
            try {
                login = sc.nextLine();
            }catch (NoSuchElementException e){
                logger.log(Level.FINE, "Error in client service. Client disconnected or no command!", e);
            }

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
        out.println("Logged in as reader.");

        while (true) {
            try {
                System.out.println("Reader waiting for request...");

                String request = readRequest(sc);

                if (request == null || request.isEmpty()) {
                    System.out.println("Reader client disconnected.");
                    break;
                }

                String[] lines = request.split("\n");
                String command = lines[0].trim();

                //System.out.println("Reader command -> " + command);

                switch (command) {

                    case "LIST_ACTIVE_DOCUMENTS" -> {

                        List<Document> activeDocs = documentService.getDocumentsWithActiveVersion();

                        if (activeDocs.isEmpty()) {
                            sendResponse(out, "No active documents available.");
                        }
                        else {
                            List<String> response = new ArrayList<>();
                            for (Document doc : activeDocs) {
                                DocumentVersion active = doc.getActiveVersion();

                                response.add(doc.getId() + " - " + doc.getTitle() + " ( Active version: " + active.getVersionNumber() + ")");
                            }
                            sendResponse(out, response.toArray(new String[0]));
                        }
                    }

                    case "VIEW_DOCUMENT" -> {

                        int docId = Integer.parseInt(lines[1].trim());
                        String content = documentService.getActiveVersionContent(docId);

                        if (content == null) {
                            sendResponse(out, "Error: Document not found or has no active version!");
                        } else {
                            sendResponse(out, content);
                        }
                    }

                    /*case "EXPORT_DOCUMENT" -> {
                        int docId = Integer.parseInt(lines[1].trim());
                        try {
                            Document doc = documentService.getDocumentById(docId);
                            DocumentVersion active = doc != null ? doc.getActiveVersion() : null;

                            if (doc == null || active == null) {

                                sendResponse(out, "Error: Document not found or has no active vresion!");

                            } else {
                                sendResponse(out, doc.getTitle(), String.valueOf(active.getVersionNumber()), active.getContent()
                                );
                            }
                        } catch (Exception e) {
                            sendResponse(out, "Error: Could not retrieve document.");
                        }
                    }*/

                    case "EXIT" -> {
                        sendResponse(out, "Goodbye!");
                        return;
                    }

                    default -> sendResponse(out, "Error: Unknown command: " + command);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in reader service", e);
                break;
            }
        }
    }

    private void reviewerMenu(Scanner sc, PrintStream out, User user) {
        out.println("Logged in as reviewer.");

        while (true) {
            try {
                System.out.println("Reviewer waiting for request...");

                String request = readRequest(sc);

                if (request == null || request.isEmpty()) {
                    System.out.println("Reviewer client disconnected.");
                    break;
                }

                String[] lines = request.split("\n");
                String command = lines[0].trim();

                System.out.println("Reviewer command -> " + command);

                switch (command) {

                    case "LIST_DOCUMENTS" -> {
                        List<Document> docs = documentService.getAllDocuments();

                        if (docs.isEmpty()) {
                            sendResponse(out, "No documents in the system.");
                        } else {

                            List<String> response = new ArrayList<>();

                            for (Document doc : docs) {
                                response.add(doc.getId() + " - " + doc.getTitle() + " (" + doc.getDocumentType() + ") ");
                            }

                            sendResponse(out, response.toArray(new String[0]));
                        }
                    }

                    case "VIEW_VERSIONS" -> {
                        int docId = Integer.parseInt(lines[1].trim());
                        try {
                            List<DocumentVersion> versions = documentService.getVersions(docId);

                            if (versions == null || versions.isEmpty()) {
                                sendResponse(out, "Document has no versions.");
                            } else {

                                List<String> response = new ArrayList<>();

                                for (DocumentVersion v : versions) {
                                    response.add("Version; " + v.getVersionNumber() + " | Author: " + v.getAuthorId() + " | Created: " + v.getCreatedAt() + " | Status: " + v.getStatus());
                                }

                                sendResponse(out, response.toArray(new String[0]));
                            }
                        } catch (NullPointerException e) {
                            sendResponse(out, "Error: Document does not exist!");
                        }
                    }

                    case "VIEW_VERSION_CONTENT" -> {
                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());

                        String content = documentService.getDraftContent(docId, versionNumber);

                        if (content == null) {
                            sendResponse(out, "Error: Document or version does not exist.");
                        } else {
                            sendResponse(out, content);
                        }
                    }

                    case "APPROVE_VERSION" -> {

                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());

                        String result = documentService.approveVersion(docId, versionNumber);
                        sendResponse(out, result);
                    }

                    case "REJECT_VERSION" -> {
                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());

                        String result = documentService.rejectVersion(docId, versionNumber);
                        sendResponse(out, result);
                    }

                    case "ADD_COMMENT" -> {

                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());
                        String comment = lines.length > 3 ? lines[3].trim() : ""; //if comment, get, if not, leave empty
                        int reviewerId = user.getUserId();

                        String result = documentService.addCommentToVersion(docId, versionNumber, reviewerId, comment);
                        sendResponse(out, result);
                    }

                    case "ACTIVATE_VERSION" -> {

                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());

                        String result = documentService.activateVersion(docId, versionNumber);
                        sendResponse(out, result);
                    }

                    case "EXIT" -> {
                        sendResponse(out, "Goodbye!");
                        return;
                    }

                    default -> sendResponse(out, "Error: Unknown command: " + command);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in reviewer service", e);
                break;
            }
        }
    }






    private void adminMenu(Scanner sc, PrintStream out, User admin)
    {
        out.println("Logged in as admin.");

        System.out.println("\nEntered as admin!\n");
        while (true) {
            try {
                System.out.println("Admin waiting for request...");

                String request = readRequest(sc);

                if (request == null || request.isEmpty()) {
                    System.out.println("Admin client disconnected.");
                    break;
                }

                String[] lines = request.split("\n");
                String command = lines[0].trim();

                System.out.println("Admin command -> " + command);

                switch (command) {

                    case "CREATE_USER" -> {
                        try {

                            Role role = Role.valueOf(lines[1].trim().toUpperCase()); //lines[1-3] role, username, password
                            String userName = lines[2].trim();
                            String password = lines[3].trim();

                            userManager.registerUser(userName, password, role);

                            sendResponse(out, "User '" + userName + "' created with role " + role + ".");

                        } catch (IllegalArgumentException e) {
                            sendResponse(out, "Error! Invalid role. Available roles: " + Arrays.toString(Role.values()));
                        } catch (UserCreationException e) {
                            sendResponse(out, "Error: " + e.getMessage());
                        }
                    }

                    case "LIST_USERS" -> {
                        List<User> users = userManager.getAllUsers();
                        if (users.isEmpty()) {
                            sendResponse(out, "No users registered.");
                        } else {
                            List<String> response = new ArrayList<>();
                            for (User user : users) {
                                response.add("ID: " + user.getUserId() + " | Username: " + user.getUserName() + " | Role: " + user.getUserRole());
                            }
                            sendResponse(out, response.toArray(new String[0]));
                        }
                    }


                    case "CHANGE_ROLE" -> {//Changes the role of the user with the given ID to the provided one.
                        try {

                            int userId =  Integer.parseInt(lines[1].trim());
                            Role newRole = Role.valueOf(lines[2].trim().toUpperCase());

                            String result = userManager.changeUserRole(userId, newRole);
                            sendResponse(out, result);

                        } catch (NumberFormatException e) {
                            sendResponse(out, "Error: User ID must be an integer.");
                        } catch (IllegalArgumentException e) {
                            sendResponse(out, "Error: Invalid role. Available roles: " + Arrays.toString(Role.values()));
                        }
                    }

                    case "LIST_DOCUMENTS" -> { //Lists all documents

                        List<Document> docs = documentService.getAllDocuments();

                        if (docs.isEmpty()) {
                            sendResponse(out, "No documents in the system.");
                        } else {

                            List<String> response = new ArrayList<>();

                            for (Document doc : docs) {

                                String active = doc.getActiveVersion() != null ? "Active v" + doc.getActiveVersion().getVersionNumber() : "No active version";
                                response.add("ID: " + doc.getId() + " | " + doc.getTitle() + " | Type: " + doc.getDocumentType() + " | " + active + " | Versions: " + doc.getAllVersions().size());

                            }
                            sendResponse(out, response.toArray(new String[0]));
                        }
                    }

                    case "EXIT" -> {
                        sendResponse(out, "Goodbye!");
                        return;
                    }

                    default -> sendResponse(out, "Error: Unknown command! " + command);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in admin service!", e);
                break;
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
                String command = lines[0].trim();

                System.out.println("\nThe command is -> " + command + "\n");

                switch (command) {

                    case "CREATE_DOCUMENT" -> { //#1
                        String title = lines[1].trim();
                        String description = lines[2].trim();
                        String documentType = lines[3].trim();
                        int authorId = author.getUserId();
                        try {

                            documentService.createDocument(title, description, authorId, DocumentType.valueOf(documentType));
                            sendResponse(out, "Document created!");
                        } catch (DocumentCreationException e ){
                            String content = "Cannot create document! Invalid data!";
                            sendResponse(out, content);
                        }
                    }

                    case "CREATE_VERSION" -> { //#2
                        int docId =  Integer.parseInt(lines[1].trim());
                        String content = lines[2].trim();
                        int authorId = author.getUserId();

                        documentService.addVersionToDocument(docId, content, authorId);

                        sendResponse(out, "OK", "Version created!");
                    }

                    case "LIST_DOCUMENTS" -> { //#3
                        List<Document> docs = documentService.getAllDocuments();

                        List<String> response = new ArrayList<>();
                        for (Document document : docs) {
                            response.add(document.getId() + " - " + document.getTitle());
                        }

                        sendResponse(out, response.toArray(new String[0]));
                    }


                    case "VIEW_VERSIONS" -> { // view the documents info only, maybe should rename to LIST_VERSIONS?
                        int docId = Integer.parseInt(lines[1].trim());
                        try {
                            List<DocumentVersion> versions = documentService.getVersions(docId);

                            List<String> response = new ArrayList<>();

                            for (DocumentVersion v : versions) {

                                response.add("Version " + v.getVersionNumber() + " - " + v.getStatus());
                            }

                            sendResponse(out, response.toArray(new String[0]));
                        } catch (NullPointerException e) {
                            String content = "Document does not exist!";
                            sendResponse(out, content);
                        }
                    }

                    case "VIEW_DRAFT" -> { //#5
                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());
//                        if(lines[3] != null){
//                            int versionNumber2 = Integer.parseInt(lines[3]);
//                            String[] content = documentService.getDraftContentForTwoVersions(docId, versionNumber, versionNumber2);
//                            sendResponse(out, content);
//                        }
//                        else{
//                            String content = documentService.getDraftContent(docId, versionNumber);
//                            sendResponse(out, content);
//                        }
                        try {
                            String content = documentService.getDraftContent(docId, versionNumber);
                            if (content == null){
                                content = "Document/Version does not exist!";
                            }
                            sendResponse(out, content);
                        } catch (NullPointerException e) {
                            String content = "Document does not exist!2";
                            sendResponse(out, content);
                        }

                    }
                    case "EDIT_DRAFT" -> { //#6

                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());
                        String newContent = lines[3].trim();
                        int authorId = author.getUserId();

                        try{

                            String content = documentService.editDraftDocument(docId, versionNumber, newContent, authorId);
                            sendResponse(out, content);

                        } catch (NullPointerException e) {
                            String content = "Document does not exist!2";
                            sendResponse(out, content);
                        }
                    }
                    case "COMPARE_VERSIONS" -> { //#7
                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());
                        int versionNumber2 = Integer.parseInt(lines[3].trim());
                        String[] contentArr = null;

                        try {

                            contentArr = documentService.getDraftContentForTwoVersions(docId, versionNumber, versionNumber2);
                            String content = null;

                            if (contentArr != null) {
                                content = contentArr[0] + "##TWO_VERSIONS###" + contentArr[1];
                                System.out.println("This is the content -> " + content);
                            }

                            if(contentArr[1] == null)
                            {
                                content = "One or more of the version numbers do not exist!";
                            }

                            System.out.println(content);
                            sendResponse(out, content);
                        } catch (IllegalArgumentException e) {

                            logger.log(Level.SEVERE, "Error: incorrect argumenst!", e);
                            //System.out.println("Error: Invalid version number!");
                            String content = "One or more of the version numbers do not exist!";
                            sendResponse(out, content);

                        } catch (NullPointerException e){

                            logger.log(Level.SEVERE, "Error occurred while loading documents", e);
                            String content = "One or more of the version numbers do not exist!";
                            sendResponse(out, content);

                        }
                    }


                    case "LIST_DRAFTS" -> { //#8

                        int docId = Integer.parseInt(lines[1]);

                        try {

                            List<DocumentVersion> versions = documentService.getAllDraftDocuments(docId);
                            //System.out.println(versions);

                            List<String> response = new ArrayList<>();

                            for (DocumentVersion version : versions) {
                                response.add(version.getContent() + " - " + version.getStatus());
                            }
                            sendResponse(out, response.toArray(new String[0]));

                        } catch (NullPointerException e) {
                            String content = "Document has no versions!";
                            sendResponse(out, content);
                        }

                    }

                    case "VIEW_DOCUMENT_HISTORY" -> { //#9
                        int docId = Integer.parseInt(lines[1]);
                        try {

                            ArrayList<DocumentVersion> versions = documentService.getVersions(docId);

                            if (versions == null || versions.isEmpty()) {
                                sendResponse(out, "No history found for this document.");
                            } else {

                                List<String> response = new ArrayList<>();

                                for (int i = 0; i < versions.size(); i++) {
                                    DocumentVersion version = versions.get(i);
                                    response.add("Version " + version.getVersionNumber() + " | Author ID: " + version.getAuthorId() + " | Created: " + version.getCreatedAt().toString() + " | Status: " + version.getStatus());
                                }
                                sendResponse(out, response.toArray(new String[0]));
                            }
                        } catch (NullPointerException e) {
                            sendResponse(out, "Document does not exist!");
                        }
                    }

                    case "REQUEST_DOCUMENT_TYPES" -> {
                        List<String> documentTypes = documentService.getDocumentTypes();
                        sendResponse(out, documentTypes.toArray(new String[0]));
                    }

                    case "SAVE_VERSION" -> {

                        int docId = Integer.parseInt(lines[1].trim());
                        int versionNumber = Integer.parseInt(lines[2].trim());
                        String newContent = lines[3].trim();
                        int authorId = author.getUserId();

                        try {

                            String result = documentService.editDraftDocument(docId, versionNumber, newContent, authorId);
                            sendResponse(out, "Version saved!", result != null ? result : "");

                        } catch (NullPointerException e) {
                            sendResponse(out, "Error: Document or version does not exist.");
                        }
                    }


                    case "EXIT" -> {
                        sendResponse(out, "Goodbye!");
                        return;
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
