package document.service;

import customExceptions.CredentialsException;
import customExceptions.UserCreationException;
import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;
import manager.UserManager;
import model.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public class DocumentCreator {

    private static final Logger logger = Logger.getLogger(DocumentCreator.class.getName());

    public void createNewDocuments(String content, int userId) {

        System.out.println("Creating users...");
        UserManager userManager = new UserManager();


        //User reviewer1 = new Reviewer("User1", "123");

        User reviewer1 = null;
        User reviewer2 = null;
        User author1 = null;
        User author2 = null;
        User reader1 = null;
        try {
            reviewer1 = userManager.registerUser("Reviewer1", "123", UserType.REVIEWER);
            reviewer2 = userManager.registerUser("Reviewer2", "123", UserType.REVIEWER);
            author1 = userManager.registerUser("Author1", "123", UserType.AUTHOR);
            author2 = userManager.registerUser("Author2", "123", UserType.AUTHOR);
            reader1 = userManager.registerUser("Reader1", "123", UserType.READER);
        } catch (UserCreationException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        }


        int id1 = reviewer1.getUserId();
        int id2 = reviewer2.getUserId();
        int id3 = author1.getUserId();
        int id4 = author2.getUserId();
        int id5 = reader1.getUserId();

        System.out.println("New user created. " + id1);
        System.out.println("New user created. " + id2);
        System.out.println("New user created. " + id3);
        System.out.println("New user created. " + id4);
        System.out.println("New user created. " + id5);

        System.out.println("Creating docs...");


        DocumentManager documentManager = new DocumentManager();
        DocumentService documentService = new DocumentService();

//        Document document1 = documentManager.addDocument("JavaExamples", "Java code and examples.", id3, DocumentType.TXT);
//        document1.createNewVersion("JavaFunctions is the most important thing, this means you have to learn!", id3);
//        Document document2 = documentManager.addDocument("JavaCourses", "Java coding and courses.", id3, DocumentType.XML);
//        document1.createNewVersion("JavaFunctions is the most important thing, this means you have to learn! I have added this.", id4);
//        document1.createNewVersion("JavaFunctions2", id2);


        Document document1 = documentManager.addDocument("JavaExamples","Java code and examples.", id3, DocumentType.TXT);

        documentService.addVersionToDocument(document1.getDocumentId(), "JavaFunctions is the most important thing, this means you have to learn!", id3);

        documentService.addVersionToDocument(document1.getDocumentId(),"JavaFunctions2", id2);


        Document document2 = documentManager.addDocument("JavaCourses", "Java coding and courses.", id3, DocumentType.XML);

        documentService.addVersionToDocument(document2.getDocumentId(),"JavaFunctions is the most important thing, this means you have to learn! I have added this.", id4);






        Document document3 = documentManager.addDocument("C# Courses", "c# coding and courses.", id4, DocumentType.DOCX);
        documentService.addVersionToDocument(document3.getDocumentId(), "Here you can learn about C#, step-by-step,", id4); //changed from doc2 to 3

        List<DocumentVersion> doc1versions = document1.getAllVersions();
        for (DocumentVersion documentVersion : doc1versions) {
            System.out.println(documentVersion);
        }
    }
}
