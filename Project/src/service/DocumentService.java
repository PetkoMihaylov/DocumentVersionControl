package service;

import model.DocumentVersionStatus;
import exceptions.DocumentCreationException;
import model.Document;
import model.DocumentType;
import model.DocumentVersion;

import java.util.*;

public class DocumentService {

    private static Map<Integer, Document> documents = new HashMap<>(); //this MAP is used to have in memory the DOCUMENTS;

    private final DocumentManager documentManager; //moved it to be created in class, not separately every time. Fixes the issue with multiple instances (I hope?).
    private final DocumentFileSaver documentFileSaver;

    public DocumentService() {
        this.documentManager = new DocumentManager();
        this.documentFileSaver = new DocumentFileSaver();
        //loading the documents.
        List<Document> loadedDocuments = documentManager.loadDocumentsFromFile();
        for (Document doc : loadedDocuments) {
            documents.put(doc.getDocumentId(), doc);
        }

        //sync the document ID with the highest in the file.
        int maxId = loadedDocuments.stream().mapToInt(Document::getDocumentId).max().orElse(0);
        if (maxId > 0) {
            Document.setCounter(maxId + 1);
        }
    }

   public Map<Integer, Document> getDocumentsMap() {
        return Collections.unmodifiableMap(documents); //read that this is better than just passing a map, because you can't modify it.
    }

    public List<Document> getAllDocuments() {

        if (documents.isEmpty()) {
            //System.out.println("It is -> " + documents);
            List<Document> loaded = documentManager.loadDocumentsFromFile();

            for (Document doc : loaded) {
                documents.put(doc.getDocumentId(), doc);
            }

            //System.out.println("Now it is -> " + documents);
        }
        return new ArrayList<>(documents.values());
    }

    public Document createDocument(String title, String description, int authorId, DocumentType documentType) throws DocumentCreationException {
        Document document = new Document(title, description, authorId, documentType);
        documents.put(document.getDocumentId(), document);
        documentManager.saveDocuments(new ArrayList<>(documents.values()));
        //System.out.println("Document created with ID: " + document.getDocumentId());
        return document;
    }

    public DocumentVersion addVersionToDocument(int documentId, String content, int userId) {
        Document document = documents.get(documentId);

       if (document == null) {
            //System.out.println("addVersionToDocument: No document found with ID " + documentId);
            return null;
        }


        DocumentVersion version = document.createNewVersion(content, userId);

        //System.out.println(version + " has been added to the document -> " + document.getLatestVersion());


        //documentFileSaver issue with input and output?
        documentFileSaver.saveVersionToFile(document, version);

        documentManager.saveDocuments(new ArrayList<>(documents.values()));


        //System.out.println("DOCUMENT VALUES -> " + documents.values());
        //System.out.println();
        //document.printDocumentData();
        //version.printAllVersionData();
        //System.out.println();

        return version;
    }

    public void setDocuments(Map<Integer, Document> passedDocuments) {
        documents = passedDocuments;
    }

    public void addDocument(Document document) {
        documents.put(document.getDocumentId(), document); //this adds it only here. It has no effect on the loaded,saved documents.
    }

    public void removeDocumentById(int documentId) {
        documents.remove(documentId);
    }

    public void removeDocument(Document document) {
        documents.remove(document.getDocumentId());
    }

    public Document getDocumentById(int id) {
        return documents.get(id);
    }

    public ArrayList<DocumentVersion> getVersions(int docId) {

        ArrayList<DocumentVersion> versions = null;
        if(documents.get(docId).getAllVersions() != null){
            versions = documents.get(docId).getAllVersions();
        }
        else{
            return null;
        }
        return versions;
    }

    public String getDraftContent(int docId, int versionNumber) {
        String content = null;
        if(documents.get(docId)!=null){
            //System.out.println("Enters the if for doc!");
            content = documents.get(docId).getVersionContent(versionNumber);
        }
        return content; // getVersionContext returns the correct version, had a bug with indexing, because it starts from 0.
    }

    public String[] getDraftContentForTwoVersions(int docId, int versionNumber, int versionNumber2) {
        String[] twoVersions = new String[2];
        twoVersions[0] = getDraftContent(docId, versionNumber);
        twoVersions[1] = getDraftContent(docId, versionNumber2);
        return twoVersions;
    }


    public List<String> getDocumentTypes() {
        return Collections.singletonList(Arrays.toString(DocumentType.values()));
    }

    public String editDraftDocument(int docId, int versionNumber, String newContent, int userId) {
        if (documents.get(docId)!= null){
            if(documents.get(docId).getVersion(versionNumber) != null){
                if(documents.get(docId).getVersion(versionNumber).getAuthorId() == userId && documents.get(docId).getVersion(versionNumber).getStatus()== DocumentVersionStatus.DRAFT){
                    documents.get(docId).getVersion(versionNumber).setContent(versionNumber, newContent, userId);

                    documentFileSaver.saveVersionToFile(documents.get(docId), documents.get(docId).getVersion(versionNumber));
                    documentManager.saveDocuments(new ArrayList<>(documents.values()));
                    return "Content was edited!";
                }
                else {
                    return "AuthorId not correct!";
                }

            }
            return "No such version!";
        }
        return "Document is null!";
    }


    public List<DocumentVersion> getAllDraftDocuments(int docId) {
        List<DocumentVersion> versions = new ArrayList<>();

        if (!documents.isEmpty() && documents.get(docId).getAllVersions() != null) {
            //System.out.print("Here1");

            for (DocumentVersion version : documents.get(docId).getAllVersions()) {
                //System.out.print("Here2");

                if (version.getStatus() == DocumentVersionStatus.DRAFT) {
                    //System.out.print("Here3");
                    versions.add(version);
                }
            }
        }

        //System.out.println("These are the versions -> " + versions);
        return versions;
    }

//    public String[] getDocumentHistory(int docId){
//        String[] history = new String[0];
//        ArrayList<DocumentVersion> versions = documents.get(docId).getAllVersions();
//        return history;
//    }

    public String approveVersion(int docId, int versionNumber) {

        Document document = documents.get(docId);

        if (document == null) {
            return "Document does not exist!";
        }


        DocumentVersion version = document.getVersion(versionNumber);

        if (version == null) {
            return "Version does not exist!";
        }

        if (version.getStatus() != DocumentVersionStatus.DRAFT) {
            return "Error: Only DRAFT versions can be approved. Current status: " + version.getStatus();
        }

        version.setStatus(DocumentVersionStatus.APPROVED);

        documentFileSaver.saveVersionToFile(document, version);
        documentManager.saveDocuments(new ArrayList<>(documents.values()));


        return "Version " + versionNumber + " of document '" + document.getTitle() + "' has been APPROVED.";
    }

    public String rejectVersion(int docId, int versionNumber) {

        Document document = documents.get(docId);
        if (document == null) return "Document does not exist!";

        DocumentVersion version = document.getVersion(versionNumber);
        if (version == null) return "Version does not exist!";

        if (version.getStatus() != DocumentVersionStatus.DRAFT) {
            return "Error: Only DRAFT versions can be rejected. Current status: " + version.getStatus();
        }

        version.setStatus(DocumentVersionStatus.REJECTED);

        documentManager.saveDocuments(new ArrayList<>(documents.values()));

        return "Version " + versionNumber + " of document '" + document.getTitle() + "' has been REJECTED.";
    }

    public String activateVersion(int docId, int versionNumber) {

        Document document = documents.get(docId);
        if (document == null) {
            return "Document does not exist!";
        }

        //System.out.println("From activateVersion version -> " + document.getVersion(versionNumber));
        DocumentVersion version = document.getVersion(versionNumber);
        if (version == null) {
            return "Version does not exist!";
        }

        if (version.getStatus() != DocumentVersionStatus.APPROVED) {
            return "Error: Only APPROVED versions can be activated. Current status: " + version.getStatus();
        }

        DocumentVersion currentActive = document.getActiveVersion();
        if (currentActive != null) {
            currentActive.setStatus(DocumentVersionStatus.APPROVED);
        }

        version.setStatus(DocumentVersionStatus.ACTIVE);
        documentManager.saveDocuments(new ArrayList<>(documents.values()));
        return "Version " + versionNumber + " is now the ACTIVE version of '" + document.getTitle() + "'.";
    }

    public String addCommentToVersion(int docId, int versionNumber, int reviewerId, String comment) {

        Document document = documents.get(docId);

        //System.out.println("From addCommentToVersion document -> " + document.getDocumentById(docId));
        if (document == null) {
            return "Document does not exist!";
        }

        DocumentVersion version = document.getVersion(versionNumber);
        //System.out.println("From addCommentToVersion version -> " + document.getVersion(versionNumber));
        if (version == null) {
            return "Version does not exist!";
        }

        version.addComment(reviewerId, comment);
        documentManager.saveDocuments(new ArrayList<>(documents.values()));

        return "Comment added to version " + versionNumber + " of '" + document.getTitle() + "'.";
    }

    public String getActiveVersionContent(int docId) {

        Document document = documents.get(docId);

        if (document == null) {
            return null;
        }
        DocumentVersion active = document.getActiveVersion();
        if (active == null) {
            return null;
        }

        return active.getContent();
    }

    public List<Document> getDocumentsWithActiveVersion() {

        List<Document> result = new ArrayList<>();

        for (Document doc : documents.values()) {
            if (doc.getActiveVersion() != null) {
                result.add(doc);
            }
        }

        return result;
    }

    public List<DocumentVersion> getPendingVersions(int docId) {

        List<DocumentVersion> pendingVersions = new ArrayList<>();
        Document document = documents.get(docId);

        if (document == null) return pendingVersions;

        for (DocumentVersion v : document.getAllVersions()) {
            if (v.getStatus() == DocumentVersionStatus.DRAFT) {
                pendingVersions.add(v);
            }
        }

        return pendingVersions;
    }




}
