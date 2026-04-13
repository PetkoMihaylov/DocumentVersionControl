package document.service;

import document.model.Document;


import java.util.*;

public class DocumentService {

    public static Map<Integer, Document> documents = new HashMap<>();

    public Map<Integer, Document> getDocumentsMap() {
        return documents;
    }
    public List<Document> getDocuments() {
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
        documents =  passedDocuments;
    }
    public void addDocument(Document document) {
        documents.put(document.getDocumentId(), document);
    }

    public void removeDocument(Document document) {
        documents.remove(document.getDocumentId());
    }


    public Document getDocumentById(int id) {
        return documents.get(id);
    }
}