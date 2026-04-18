package document.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Document implements Serializable {

    private static final AtomicInteger documentCounter = new AtomicInteger(1);
    private final int documentId;
    private String title;
    private String description;
    private int authorId;
    private DocumentType documentType;
    private ArrayList<String> commentsByReviewer;

    //private List<Document> documents = new ArrayList<>();
    private ArrayList<DocumentVersion> versions;

    public Document(String title, String description, int authorId, DocumentType documentType) {
        System.out.println("Print from Document -> counter-> " + documentCounter);
        this.documentId = documentCounter.getAndIncrement();
        System.out.println("Print from Document -> counterAFTERIncrement-> " + documentCounter);
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.documentType = documentType;
        this.commentsByReviewer = new ArrayList<>();
        this.versions = new ArrayList<>();
    }


//    public int setVersionsCounter(DocumentVersion version) {
//
//        int maxId=1;
//        for (DocumentVersion version : versions){
//            if(version.getVersionNumber() > maxId){
//                maxId = version.getVersionNumber() + 1;
//                version.setCounter(maxId);
//            }
//        }
//        return maxId;
//    }
    public static void setCounter(int value) {
        documentCounter.set(value);
        System.out.println("Print from Document.setCounter -> counter-> " + documentCounter);
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

    public DocumentVersion createNewVersion(String content, int userId) {
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
        return version;
    }

    public DocumentVersion getLatestVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.getLast();
    }

    public List<DocumentVersion> getAllVersions() {
        if (versions.isEmpty()) {
            return null;
        }
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

    public int getId() {
        return documentId;
    }

    public String getVersionContent(int versionNumber) {
        if(versions.isEmpty()) {
            System.out.println("I am from getVersionContent and versions is empty!\n");
            //DocumentManager documentManager = new DocumentManager();
            //documentManager.loadDocuments();
        }
        return versions.get(versionNumber).getContent();
    }
    public DocumentVersion getVersion(int versionNumber) {
        return versions.get(versionNumber);
    }

    public ArrayList<DocumentVersion> getVersions() {
        return versions;
    }

    public boolean versionsIsEmpty() {
        if(versions.isEmpty()){
            return true;
        }
        return false;
    }
}