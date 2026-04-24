package client.service;

import client.ui.LanternaEditor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.service.ClientUserMenuService.readResponse;
import static client.service.ClientUserMenuService.sendCommand;

public class ReaderMenu {

    private static final Logger logger = Logger.getLogger(ReaderMenu.class.getName());

    public static void showReaderMenu(Scanner console, Scanner sc, PrintStream out) {

        while (true) {
            System.out.println("""
                --- Reader Menu ---
                1. List Active Documents
                2. View Document (active version)
                3. Export To PDF/TXT (NOT WORKING!)
                0. Exit
                """);

            int choice;

            try {
                choice = Integer.parseInt(console.nextLine().trim());
            } catch (NumberFormatException ex) {
                logger.log(Level.WARNING, "Invalid number!");
                continue;
            }

            switch (choice) {

                case 1 -> { //LIST_ACTIVE_DOCUMENTS
                    sendCommand(out, "LIST_ACTIVE_DOCUMENTS");
                    System.out.println(readResponse(sc));
                }

                case 2 -> { // VIEW_DOCUMENT
                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();

                    sendCommand(out, "VIEW_DOCUMENT", docId);
                    String content = readResponse(sc);

                    if (content.startsWith("Error")) {
                        System.out.println(content);
                    } else {
                        LanternaEditor viewer = new LanternaEditor(content, null); //only view
                        try {
                            viewer.startView();
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Could not start Lanterna editor/viewer!", e);

                            System.out.println(content);
                        } catch (NullPointerException e) {
                            logger.log(Level.SEVERE, "Could not start Lanterna editor/viewer!", e);
                        }
                    }
                }

                case 3 -> { // EXPORT_DOCUMENT
                    sendCommand(out, "LIST_ACTIVE_DOCUMENTS");
                    System.out.println(readResponse(sc));

                    System.out.print("Document ID: ");
                    String docId = console.nextLine().trim();

                    sendCommand(out, "EXPORT_DOCUMENT", docId);
                    String response = readResponse(sc);

                    if (response.startsWith("Error")) {
                        System.out.println(response);
                        break;
                    }

                    String[] parts = response.split("\n", 3);
                    if (parts.length < 3) {
                        System.out.println("Error: Unexpected response from server.");
                        break;
                    }

                    String title    = parts[0].trim();
                    String version  = parts[1].trim();
                    String content  = parts[2].trim();

                    System.out.print("What format do you want to export to (TXT / PDF): ");
                    String format = console.nextLine().trim().toUpperCase();


                    String suggestedName = title.replaceAll("\\s+", "_") + "_v" + version;
                    System.out.print("Filename (without extension) [" + suggestedName + "]: ");
                    String input = console.nextLine().trim();
                    String filename = input.isEmpty() ? suggestedName : input;

                    try {
                        if (format.equals("PDF")) {
                            DocumentExporter.exportToPdf(title, version, content, filename);
                        } else if (format.equals("TXT")) {
                            DocumentExporter.exportToTxt(title, version, content, filename);
                        } else {
                            System.out.println("Unknown format. Please enter TXT or PDF.");
                        }
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Export failed!", e);
                        System.out.println("Export failed: " + e.getMessage());
                    }
                }

                case 0 -> { //EXIT
                    sendCommand(out, "EXIT");
                    readResponse(sc);
                    System.out.println("Logged out from reader service.");
                    return;
                }

                default -> System.out.println("Invalid choice. Please enter a number from the service.");
            }
        }
    }

}
