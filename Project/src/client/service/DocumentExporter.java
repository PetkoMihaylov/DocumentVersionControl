package client.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class DocumentExporter {

    public static void exportToTxt(String title, String version, String content, String filename) throws IOException {
        String path = filename.endsWith(".txt") ? filename : filename + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(path, StandardCharsets.UTF_8))) {
            writer.printf("Title: %s%n", title);
            writer.printf("Version: %s%n", version);
            writer.println("-----");
            writer.println(content);
        }

        System.out.println("Exported to: " + path);
    }

    public static void exportToPdf(String title, String version, String content, String filename) throws IOException {
        String path = filename.endsWith(".pdf") ? filename : filename + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);

            cs.beginText();
            cs.newLineAtOffset(1, 500);

            cs.showText("Title: " + title);
            cs.newLineAtOffset(0, -15);
            cs.showText("Version: " + version);
            cs.newLineAtOffset(0, -15);
            cs.showText("-----");
            cs.newLineAtOffset(0, -15);

            String[] lines = content.split("\n");
            for (String line : lines) {
                line = line.replaceAll("[^\\x00-\\x7F]", "?"); //removes unsopported characters.

                cs.showText(line);
                cs.newLineAtOffset(0, -15);
            }

            cs.endText();
            cs.close();

            document.save(path);
        }

        System.out.println("Exported to: " + path);
    }
}