package client.service;

import client.ui.LanternaEditor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.service.ClientUserMenuService.readResponse;
import static client.service.ClientUserMenuService.sendCommand;

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
                5. View Draft of specific document and version
                6. Edit Draft of specific document and version
                7. Compare versions of a document
                8. List Drafts
                9. View Document History
                0. Exit
                """);

            int choice;
            try {
                choice = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException ex) {
                logger.log(Level.WARNING, "Invalid entry!");
                continue;
            }

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
                    String response = readResponse(sc);
                    //if(response.equals("Cannot create document!")){

                    //}
                    System.out.println(response);
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

                case 4 -> { //VIEW_VERSIONS, but it is essentially LIST_VERSIONS
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();

                    sendCommand(out,"VIEW_VERSIONS", docId);
                    String response = readResponse(sc);
                    //if (response == "Document does not exist!"){
                    //}
                    System.out.println(response);
                }
                case 5 -> { //VIEW_DRAFT
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();
                    System.out.print("Version Number: ");
                    String versionNumber = console.nextLine();

                    sendCommand(out, "VIEW_DRAFT", docId, versionNumber);
                    String content = readResponse(sc);
                    if(content.contains("Document/Version does not exist!") || content.contains("Document does not exist!2"))
                    {
                        System.out.println(content);
                    }
                    else {

                        // opening LanternaEditor
                        LanternaEditor editor = new LanternaEditor(content, null);

                        try {
                            editor.startView(); //start a view
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
                            break;
                        }
                    }
                }

                case 6 -> { //EDIT_DRAFT which is (VIEW+EDIT)
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();
                    System.out.print("Version Number: ");
                    String versionNumber = console.nextLine();
//                    System.out.print("See old version (draft) in parallel while editing? Y/N");
//                    String oldVersionView = console.nextLine().equalsIgnoreCase("Y") ? "Yes" : "No";

                    sendCommand(out, "VIEW_DRAFT", docId, versionNumber);
                    String content = readResponse(sc);

                    LanternaEditor editor = null;
                    if(content.contains("Document/Version does not exist!") || content.contains("Document does not exist!2"))
                    {
                        System.out.println(content);
                        break;
                    }
                    else{
                            editor = new LanternaEditor(content, (newContent) -> {
                                sendCommand(out, "SAVE_VERSION", docId, versionNumber, newContent);
                                String saveResponse = readResponse(sc);
                                System.out.println("Save response: " + saveResponse);
                            });

                        try {
                            editor.startEditWithCompare();
                            // The post-edit EDIT_DRAFT send is still fine for when the user exits:
                            String edited = editor.getEditedText();
                            sendCommand(out, "EDIT_DRAFT", docId, versionNumber, edited);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
                            break;
                        }
                        System.out.println(readResponse(sc));
                    }



                    /*else {

                        // opening LanternaEditor
                        editor = new LanternaEditor(content);

                        try {
                            editor.startEditWithCompare(); //start a view
                            String edited = editor.getEditedText();
                            System.out.println("This is the edited value in EDIT_DRAFT -> " + edited);
                            sendCommand(out,"EDIT_DRAFT", docId, versionNumber, edited);

                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
                            break;
                        }
                    }*/

//                    if(oldVersionView.equalsIgnoreCase("Yes")) {
//                        try {
//                            editor.startEditWithCompare(); // start an edit with new version and compare on left
//                        } catch (IOException e) {
//                            logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
//                        }
//                    }
//                    else {
//                        try {
//                            editor.startEdit(); // start an edit based on new version but with no view of old
//                        } catch (IOException e) {
//                            logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
//                        }
//                    }
                    // after editing
                    System.out.println(readResponse(sc));
                }
                case 7 -> { // COMPARE_VERSIONS.
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();
                    System.out.print("Version Number 1: ");
                    String versionNumber = console.nextLine();
                    System.out.print("Version Number 2: ");
                    String versionNumber2 = console.nextLine();

                    sendCommand(out, "COMPARE_VERSIONS", docId, versionNumber, versionNumber2);
                    String response = readResponse(sc);
                    String[] content = null;
                    //System.out.println(response);
                    if(response.contains("##TWO_VERSIONS###")){
                        content = response.split("##TWO_VERSIONS###");
                        try {
                            //assert content != null;
                            LanternaEditor editor = new LanternaEditor(content[0], null);
                            try {
                                editor.startCompare(content[0], content[1]); //start a compare with
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Could not start Lanterna Editor", e);
                            }

                        } catch (NullPointerException e){
                            logger.log(Level.SEVERE, "Content is null!", e);
                        }
                    }
                    else if (response.equals("One or more of the version numbers do not exist!")){
                        System.out.println(response);
                    }
                    else {
                        System.out.println(response); //some other error
                    }



                }
                case 8 -> {
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();
                    sendCommand(out, "LIST_DRAFTS", docId);
                    String response = readResponse(sc);
                    //String[] content = null;
                    System.out.println(response);

                }

                case 9 -> { //VIEW_DOCUMENT_HISTORY
                    System.out.print("Document ID: ");
                    String docId = console.nextLine();

                    sendCommand(out, "VIEW_DOCUMENT_HISTORY", docId);
                    String response = readResponse(sc);
                    System.out.println(response);
                }


                case 0 -> {
                    sendCommand(out, "EXIT");
                    readResponse(sc);
                    System.out.println("Logged out from the author service!");
                    return;
                }
            }
        }
    }
}