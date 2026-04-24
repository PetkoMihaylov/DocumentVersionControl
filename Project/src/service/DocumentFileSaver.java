package service;

import model.Document;
import model.DocumentType;
import model.DocumentVersion;

import java.io.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DocumentFileSaver {

    private static final Logger logger = Logger.getLogger(DocumentFileSaver.class.getName());

    private static final String BASE_DIRECTORY = "document_files";


    public void saveVersionToFile(Document document, DocumentVersion version) {

        String docDir = BASE_DIRECTORY + File.separator + "doc_" + document.getDocumentId();
        try {
            
            Files.createDirectories(Paths.get(docDir));
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create directory for document files: " + docDir, e);
            return;
        }

        String extension = getFileExtension(DocumentType.valueOf(document.getDocumentType()));

        String filePath = docDir + File.separator + "v" + version.getVersionNumber() + "." + extension;

        try {
            
            switch (DocumentType.valueOf(document.getDocumentType())) {

                case TXT  -> {
                    saveTxt(filePath, document, version);
                }
                case JSON -> {
                    saveJson(filePath, document, version);
                }
                case XML  -> {
                    saveXml(filePath, document, version);
                }

                // DOC and DOCX saving them as plain text because of binary?
                case DOC -> {
                    savePlainTextWithHeader(filePath, document, version, "DOC");
                }
                case DOCX -> {
                    savePlainTextWithHeader(filePath, document, version, "DOCX");
                }
            }
            logger.log(Level.INFO, "Saved version {0} of document {1} to ''{2}''", //{} make it so it gets info from the line below, like in Javascript with {}
            new Object[]{version.getVersionNumber(), document.getId(), filePath});
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write document version to file: " + filePath, e);
        }
    }

    private String getFileExtension(DocumentType type) {
        return switch (type) {
            case TXT  -> "txt";
            case JSON -> "json";
            case XML  -> "xml";
            case DOC  -> "doc";
            case DOCX -> "docx";
        };
    }

    private void saveTxt(String filePath, Document document, DocumentVersion version) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            writer.write("----- Document: " + document.getTitle() + " -----");
            writer.newLine();
            documentInfoWriter(document, version, writer);
            writer.write(version.getContent()); //writes the document content below the header.

        }
    }

    private static void documentInfoWriter(Document document, DocumentVersion version, BufferedWriter writer) throws IOException {

        writer.write("Description: " + document.getDescription());
        writer.newLine();
        writer.write("Document ID : " + document.getId());
        writer.newLine();
        writer.write("Version : " + version.getVersionNumber());
        writer.newLine();
        writer.write("Author ID : " + version.getAuthorId());
        writer.newLine();
        writer.write("Created At : " + version.getCreatedAt());
        writer.newLine();
        writer.write("Status: " + version.getStatus());
        writer.newLine();
        writer.write("----- Content -----");
        writer.newLine();
    }

    private void saveJson(String filePath, Document document, DocumentVersion version) throws IOException {
        String json = "{\n"
                + "  \"DocumentId\": " + document.getId() + ",\n"
                + "  \"Title\": \"" + escapeJson(document.getTitle()) + "\",\n"
                + "  \"Description\": \"" + escapeJson(document.getDescription()) + "\",\n"
                + "  \"DocumentType\": \"" + escapeJson(document.getDocumentType()) + "\",\n"
                + "  \"Version\": " + version.getVersionNumber() + ",\n"
                + "  \"AuthorId\": " + version.getAuthorId() + ",\n"
                + "  \"CreatedAt\": \"" + version.getCreatedAt() + "\",\n"
                + "  \"Status\": \"" + version.getStatus() + "\",\n"
                + "  \"Content\": \"" + escapeJson(version.getContent()) + "\"\n"
                + "}";
        //writing the JSON string to the file
        Files.writeString(Paths.get(filePath), json);
    }

    private void saveXml(String filePath, Document document, DocumentVersion version) throws IOException {
        //is it good to do this manually?
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<document>\n"
                + "  <documentId>" + document.getId() + "</documentId>\n"
                + "  <title>" + escapeXml(document.getTitle()) + "</title>\n"
                + "  <description>" + escapeXml(document.getDescription()) + "</description>\n"
                + "  <documentType>" + escapeXml(document.getDocumentType()) + "</documentType>\n"
                + "  <version>" + version.getVersionNumber() + "</version>\n"
                + "  <authorId>" + version.getAuthorId() + "</authorId>\n"
                + "  <createdAt>" + version.getCreatedAt() + "</createdAt>\n"
                + "  <status>" + version.getStatus() + "</status>\n"
                + "  <content><![CDATA[" + version.getContent() + "]]></content>\n" //read that CDATA allow saving multi-line text and special characters (UTF-8?)
                + "</document>";
        Files.writeString(Paths.get(filePath), xml);
    }

    private void savePlainTextWithHeader(String filePath, Document document,
                                         DocumentVersion version, String format) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("----- " + format + " Document (plain-text export) -----");
            writer.newLine();
            writer.write("Full " + format + " binary format requires Apache POI.");
            writer.newLine();
            writer.write("Title : " + document.getTitle());
            writer.newLine();
            documentInfoWriter(document, version, writer);
            writer.write(version.getContent());
        }
    }

    private String escapeJson(String input) {

        if (input == null) return "";

        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")   // must be first
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}