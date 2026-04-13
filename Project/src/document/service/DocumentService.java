package document.service;

import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;
import document.model.DocumentVersionStatus;
import model.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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