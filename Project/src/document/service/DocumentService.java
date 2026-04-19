package document.service;

import exceptions.DocumentCreationException;
import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;

import java.util.*;

public class DocumentService {

    private static Map<Integer, Document> documents = new HashMap<>(); //this MAP is used to have in memory the DOCUMENTS;

    private final DocumentManager documentManager; //moved it to be created in class, not separately every time. Fixes the issue with multiple instances (I hope?).

    public DocumentService() {
        this.documentManager = new DocumentManager();
        //loading the documents.
        List<Document> loadedDocuments = documentManager.loadDocumentsFromFile();
        for (Document doc : loadedDocuments) {
            documents.put(doc.getDocumentId(), doc);
        }

        //sync the document ID with the highest in the file
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
            System.out.println("It is -> " + documents);
            List<Document> loaded = documentManager.loadDocumentsFromFile();
            for (Document doc : loaded) {
                documents.put(doc.getDocumentId(), doc);
            }
            System.out.println("Now it is -> " + documents);
        }
        return new ArrayList<>(documents.values());
    }

    public Document createDocument(String title, String description, int authorId, DocumentType documentType) throws DocumentCreationException {
        Document document = new Document(title, description, authorId, documentType);
        documents.put(document.getDocumentId(), document);
        documentManager.saveDocuments(new ArrayList<>(documents.values()));
        System.out.println("Document created with ID: " + document.getDocumentId());
        return document;
    }

    public DocumentVersion addVersionToDocument(int documentId, String content, int userId) {
        Document document = documents.get(documentId);

       if (document == null) {
            System.out.println("addVersionToDocument: No document found with ID " + documentId);
            return null;
        }


        DocumentVersion version = document.createNewVersion(content, userId);

        System.out.println(version + " has been added to the document -> " + document.getLatestVersion());

        documentManager.saveDocuments(new ArrayList<>(documents.values()));

        System.out.println("DOCUMENT VALUES -> " + documents.values());
        System.out.println();
        document.printDocumentData();
        version.printAllVersionData();
        System.out.println();

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

    public List<DocumentVersion> getVersions(int docId) {
        List<DocumentVersion> versions = documents.get(docId).getAllVersions();
        return versions;
    }

    public String getDraftContent(int docId, int versionNumber) {
        return documents.get(docId).getVersionContent(versionNumber);
    }

    public List<String> getDocumentTypes() {
        return Collections.singletonList(Arrays.toString(DocumentType.values()));
    }
}
