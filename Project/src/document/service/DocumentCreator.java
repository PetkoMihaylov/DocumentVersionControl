package document.service;

import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;
import model.*;

import java.util.List;

public class DocumentCreator {

    public void createNewDocument(String content, int userId) {

        System.out.println("Creating users...");
        User reviewer1 = new Reviewer("User1", "123");
        int id1 = reviewer1.getUserId();
        User reviewer2 = new Reviewer("User2", "123");
        int id2 = reviewer2.getUserId();
        User author1 = new Author("Author1", "123");
        int id3 = author1.getUserId();
        User author2 = new Author("Author2", "123");
        int id4 = author2.getUserId();
        User reader1 = new Reader("Reader1", "123");
        int id5 = reader1.getUserId();


        System.out.println("Creating docs...");
        Document document1 = new Document(1, "JavaExamples", "Java code and examples.", id1, DocumentType.TXT);
        document1.createNewVersion("JavaFunctions", id1);
        Document document2 = new Document(2, "JavaCourses", "Java code and courses.", id1, DocumentType.XML);
        Document document3 = new Document(3, "C#Courses", "C# code and courses.", id2, DocumentType.DOCX);
        document1.createNewVersion("JavaFunctions2", id2);
        List<DocumentVersion> doc1versions = document1.getAllVersions();
        for (DocumentVersion documentVersion : doc1versions) {
            System.out.println(documentVersion);
        }
    }
}
