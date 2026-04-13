package document.model;
import document.service.DocumentManager;
import document.service.DocumentService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Document implements Serializable {

    private static final AtomicInteger counter = new AtomicInteger(1);
    private final int documentId;
    private String title;
    private String description;
    private int authorId;
    private DocumentType documentType;
    private ArrayList<String> commentsByReviewer;

    //private List<Document> documents = new ArrayList<>();
    private ArrayList<DocumentVersion> versions;

    public Document(String title, String description, int authorId, DocumentType documentType) {
        this.documentId = counter.getAndIncrement();
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.documentType = documentType;
        this.commentsByReviewer = new ArrayList<>();
        this.versions = new ArrayList<>();
    }


    public int getDocumentId() {
        return documentId;
    }

    public int getAuthorId () {
        return authorId;
    }
    public String getTitle() {
        return title;
    }

    public Document getDocumentById (int documentId) {
        for (DocumentVersion version : versions) {
            if (version.getVersionNumber() == documentId) {
                return this;
            }
        }
        return null;
    }

    public ArrayList<String> getCommentsByReviewer() {
        return commentsByReviewer;
    }

    public void addCommentByReviewer(String comment) {
        commentsByReviewer.add(comment);
    }

//    public void setCommentsByReviewer(String[] commentsByReviewer) {
//        this.commentsByReviewer.add(Arrays.toString(commentsByReviewer));
//    }


    public String getDocumentType() {
        return documentType.toString();
    }

    public void createNewVersion(String content, int userId) {
        int newVersionNumber = versions.size() + 1;

        DocumentVersion version = new DocumentVersion(newVersionNumber, content, userId);
        System.out.println("This is a version -> " + version);
        System.out.println("New version number -> " + newVersionNumber);
        System.out.println("New version content -> " + version.getContent());
        versions.add(version);
//        DocumentService documentService = new DocumentService();
//        documentService.addVersionToDocument(this.documentId, content, userId);

        //Document doc = DocumentService.documents.get(this.documentId);




//        DocumentManager documentManager = new DocumentManager();
//        DocumentService documentService = new DocumentService();
//        documentService.addDocument(this);
//        documentManager.saveDocuments(documentService.getDocuments());
        System.out.println("Printing from CreateNewVersion in Document.java -> " + versions);
        //return version;
    }

    public DocumentVersion getLatestVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.getLast();
    }

    public List<DocumentVersion> getAllVersions() {
        return versions;
    }

    public DocumentVersion getActiveVersion() {
        return versions.stream()
                .filter(v -> v.getStatus() == DocumentVersionStatus.ACTIVE)
                .findFirst().orElse(null);
    }

    public void printDocumentData() {

        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Author ID: " + authorId);
        System.out.println("Document ID: " + documentId);
        System.out.println("Document Type: " + documentType.toString());
        System.out.println("Comments by reviewer: " + commentsByReviewer.toString());
        System.out.println("Versions: " + versions.toString() + " -> " + versions.size());
        //System.out.println("Versions: " + this.versions.toString() + " -> " + this.versions.size());
        for (DocumentVersion version : versions) {
            System.out.println(version.getContent());
            System.out.println("Why is this not working properly? -> " + Arrays.toString(versions.toArray()));
        }
    }

}