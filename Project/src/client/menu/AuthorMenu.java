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
                4. View Versions
                5. Edit Draft
                0. Exit
                """);

            int choice = Integer.parseInt(console.nextLine());

            switch (choice) {

                case 1 -> {
                    System.out.print("Title: ");
                    String title = console.nextLine();

                    System.out.print("Content: ");
                    String content = console.nextLine();

                    sendCommand(out,"CREATE_DOCUMENT", title, content
                    );

                    System.out.println(readResponse(sc));
                }

                case 2 -> {
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();

                    System.out.print("New content: ");
                    String content = console.nextLine();

                    sendCommand(out,"CREATE_VERSION", docId, content
                    );

                    System.out.println(readResponse(sc));
                }

                case 3 -> {
                    sendCommand(out, "LIST_DOCUMENTS");
                    System.out.println(readResponse(sc));
                }

                case 4 -> {
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();

                    sendCommand(out,"VIEW_VERSIONS", docId);

                    System.out.println(readResponse(sc));
                }

                case 5 -> {
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();

                    sendCommand(out, "VIEW_DRAFT", docId);
                    String content = readResponse(sc);

                    // opening LanternaEditor
                    LanternaEditor editor = new LanternaEditor(content);

                    try {
                        editor.start();
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
                    }

                    // after editing
//                    String edited = editor.getEditedText(); // you need to add this method
//
//                    sendCommand(out,"EDIT_DRAFT", docId, edited);

                    System.out.println(readResponse(sc));
                }

                case 0 -> {
                    return;
                }
            }
        }
    }
}