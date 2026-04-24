package client.service;

import client.ui.LanternaEditor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.service.ClientUserMenuService.readResponse;
import static client.service.ClientUserMenuService.sendCommand;

public class ReviewerMenu {

    private static final Logger logger = Logger.getLogger(ReviewerMenu.class.getName());

    public static void showReviewerMenu(Scanner console, Scanner sc, PrintStream out) {

        while (true) {
            System.out.println("""
                --- Reviewer Menu ---
                1. List Documents
                2. View Versions of a Document
                3. View Version Content
                4. Approve Version
                5. Reject Version
                6. Add Comment to Version
                7. Activate Version (promote APPROVED → ACTIVE)
                0. Exit
                """);

            int choice;
            try {
                choice = Integer.parseInt(console.nextLine().trim());
            } catch (NumberFormatException ex) {
                logger.log(Level.WARNING, "Invalid entry – please enter a number.");
                continue;
            }

            switch (choice) {

                case 1 -> { //LIST_DOCUMENTS
                    sendCommand(out, "LIST_DOCUMENTS");
                    System.out.println(readResponse(sc));
                }


                //version prints with all data.
                case 2 -> { //VIEW_VERSIONS
                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();

                    sendCommand(out, "VIEW_VERSIONS", docId);
                    System.out.println(readResponse(sc));
                }

                case 3 -> { //VIEW_VERSION_CONTENT
                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();
                    System.out.print("Version Number: ");
                    String versionNumber = console.nextLine().trim();

                    sendCommand(out, "VIEW_VERSION_CONTENT", docId, versionNumber);
                    String content = readResponse(sc);


                    if (content.startsWith("Error")) {
                        System.out.println(content);
                    } else {
                        LanternaEditor viewer = new LanternaEditor(content, null); //only view
                        try {
                            viewer.startView();
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Could not start Lanterna editor/viewer", e);

                            System.out.println(content);
                        }
                    }
                }

                case 4 -> { // APPROVE_VERSION
                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();
                    System.out.print("Version Number to approve: ");
                    String versionNumber = console.nextLine().trim();

                    sendCommand(out, "APPROVE_VERSION", docId, versionNumber);
                    System.out.println(readResponse(sc));
                }

                // The version is kept in the document history.
                case 5 -> { //REJECT_VERSION
                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();
                    System.out.print("Version Number to reject: ");
                    String versionNumber = console.nextLine().trim();

                    System.out.print("Reason for rejection (optional): ");
                    String reason = console.nextLine().trim();

                    sendCommand(out, "REJECT_VERSION", docId, versionNumber);
                    String rejectResult = readResponse(sc);
                    System.out.println(rejectResult);

                    if (!reason.isEmpty() && !rejectResult.startsWith("Error")) {
                        sendCommand(out, "ADD_COMMENT", docId, versionNumber, "Rejection reason: " + reason);
                        String response = readResponse(sc);
                        System.out.print("Comment added!");
                    }
                }

                case 6 -> { //ADD_COMMENT
                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();
                    System.out.print("Version Number: ");
                    String versionNumber = console.nextLine().trim();
                    System.out.print("Comment: ");
                    String comment = console.nextLine().trim();

                    sendCommand(out, "ADD_COMMENT", docId, versionNumber, comment);
                    System.out.println(readResponse(sc));
                }


                case 7 -> { //ACTIVATE_VERSION
                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();
                    System.out.print("Version Number to activate: ");
                    String versionNumber = console.nextLine().trim();

                    sendCommand(out, "ACTIVATE_VERSION", docId, versionNumber);
                    System.out.println(readResponse(sc));
                }

                case 0 -> { //EXIT
                    sendCommand(out, "EXIT");
                    readResponse(sc);
                    System.out.println("Logged out from reviewer service.");
                    return;
                }

                default -> System.out.println("Invalid choice. Please enter a number from the service.");
            }
        }
    }

}
