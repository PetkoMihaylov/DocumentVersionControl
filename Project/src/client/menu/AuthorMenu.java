package client.menu;

import client.ui.LanternaEditor;
import document.service.DocumentService;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.menu.ClientUserMenuService.readResponse;
import static client.menu.ClientUserMenuService.sendCommand;

public class AuthorMenu {

    private static final Logger logger = Logger.getLogger(AuthorMenu.class.getName());

//    public static void showAuthorMenu(Scanner console, Scanner sc, PrintStream out) {
//        DocumentService documentService = new DocumentService();
//        LanternaEditor lanternaEditor = new LanternaEditor(documentService.getDocumentById(2).getLatestVersion().getContent());
//        System.out.println("I am in showAuthorMenu(1) -> " + documentService.getDocumentById(2).getLatestVersion().getContent());
//        try{
//            lanternaEditor.start();
//        } catch (IOException e){
//            logger.log(Level.SEVERE, "Error occurred", e);
//        }
//        System.out.println("I am in showAuthorMenu(2) -> " + sc.nextLine());
//        out.println(console.nextLine());
//
//        // LIST_DOCUMENTS
//        String next = sc.nextLine();
//        System.out.println(next);
//        if (next.startsWith("Error")) {
//            return;
//        }
//        out.println(console.nextLine());
//
//
//    }

    static void showAuthorMenu(Scanner console, Scanner sc, PrintStream out) {

        while (true) {
            System.out.println("""
                1. Create Document
                2. Create Version
                3. List Documents
                4. View Versions (of a document)
                5. View Draft
                6. Edit Draft
                7. List Drafts
                8. View Document History
                0. Exit
                """);

            int choice = Integer.parseInt(console.nextLine());

            switch (choice) {

                case 1 -> { //CREATE_DOCUMENT
                    System.out.print("Title: ");
                    String title = console.nextLine();

                    System.out.print("Description: ");
                    String description = console.nextLine();

                    sendCommand(out, "REQUEST_DOCUMENT_TYPES");
                    System.out.print(readResponse(sc));

                    System.out.println("What type of document from the listed do you want?");
                    System.out.println("Document type: ");
                    String documentType = console.nextLine();



                    sendCommand(out,"CREATE_DOCUMENT", title, description, documentType);

                    System.out.println(readResponse(sc));
                }

                case 2 -> { //CREATE_VERSION (which is LIST_DOCUMENTS + CREATE_VERSION)
                    sendCommand(out, "LIST_DOCUMENTS");
                    System.out.println(readResponse(sc));
                    System.out.println("Select a Document by the ID you see!");
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();

                    System.out.print("New content: ");
                    String content = console.nextLine();

                    sendCommand(out,"CREATE_VERSION", docId, content);

                    System.out.println(readResponse(sc));
                }

                case 3 -> { //LIST_DOCUMENTS
                    sendCommand(out, "LIST_DOCUMENTS");
                    System.out.println(readResponse(sc));
                }

                case 4 -> { //VIEW_VERSIONS
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();

                    sendCommand(out,"VIEW_VERSIONS", docId);

                    System.out.println(readResponse(sc));
                }
                case 5 -> { //VIEW_DRAFT
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();
                    System.out.print("Version Number: ");
                    String versionNumber = console.nextLine();

                    sendCommand(out, "VIEW_DRAFT", docId, versionNumber);
                    String content = readResponse(sc);

                    // opening LanternaEditor
                    LanternaEditor editor = new LanternaEditor(content);

                    try {
                        editor.startView(); //start a view
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
                    }
                }

                case 6 -> { //EDIT_DRAFT which is (VIEW+EDIT)
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();
                    System.out.print("Version Number: ");
                    String versionNumber = console.nextLine();

                    sendCommand(out, "VIEW_DRAFT", docId, versionNumber);
                    String content = readResponse(sc);

                    // opening LanternaEditor
                    LanternaEditor editor = new LanternaEditor(content);

                    try {
                        editor.startEdit(); //start an edit
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
                    }

                    // after editing
                    String edited = editor.getEditedText(); // you need to add this method

                    sendCommand(out,"EDIT_DRAFT", docId, versionNumber, edited);

                    System.out.println(readResponse(sc));
                }

                case 0 -> {
                    System.out.print("You exited!");
                    return;
                }
            }
        }
    }
}