package client.menu;

import client.ui.LanternaEditor;
import document.service.DocumentService;

import java.io.PrintStream;
import java.util.Scanner;

public class AuthorMenu {

    public static void showAuthorMenu(Scanner console, Scanner sc, PrintStream out) {
        DocumentService documentService = new DocumentService();
        LanternaEditor lanternaEditor = new LanternaEditor(documentService.getDocumentById(2).getLatestVersion().getContent());
        System.out.println(documentService.getDocumentById(2).getLatestVersion().getContent());

        System.out.println(sc.nextLine());
        out.println(console.nextLine());

        // LISTDOCUMENTS
        String next = sc.nextLine();
        System.out.println(next);
        if (next.startsWith("Error")) {
            return;
        }
        out.println(console.nextLine());


    }
}