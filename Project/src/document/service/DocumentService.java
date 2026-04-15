package document.service;

import exceptions.DocumentCreationException;
import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;


import java.util.*;

public class DocumentService {

    public static Map<Integer, Document> documents = new HashMap<>(); //this MAP is used to have in memory the DOCUMENTS;

    public Map<Integer, Document> getDocumentsMap() {
        return documents;
    }
    public List<Document> getAllDocuments() {
        if (documents.isEmpty()) {
            DocumentManager documentManager = new DocumentManager();
            System.out.println("It is -> " + documents);
            documentManager.loadDocuments();
            System.out.println("Now it is -> " + documents);
        }
        return new ArrayList<>(documents.values());
    }
    public void addVersionToDocument(int documentId, String content, int userId){
        Document document = documents.get(documentId);
        if (document != null) {
            document.createNewVersion(content, userId);

            DocumentManager documentManager = new DocumentManager();
            documentManager.saveDocuments(new ArrayList<>(documents.values()));
        }
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

    public List<DocumentVersion> getVersions(int docId) {
        List<DocumentVersion> versions = documents.get(docId).getAllVersions();
        return versions;
    }
    public void createDocument(String title, String description, int authorId, DocumentType documentType) throws DocumentCreationException {
        //this method actually calls addDocument from UserManager and it creates the user!
        //why, why did I do it like this? FIX IT ASAP!
        DocumentManager documentManager = new DocumentManager();
        documentManager.addDocument(title, description, authorId, documentType);
    }

    public void createVersion(int docId, String content, int userId) {
        addVersionToDocument(docId, content, userId);
    }

    public String getDraftContent(int docId, int versionNumber) {
        return documents.get(docId).getVersionContent(versionNumber);
    }

//    public void updateDraft(int docId, int versionNumber, String content) {
//        documents.get(docId).getVersion(versionNumber).setContent(content);
//    }
}