package document.service;

import customExceptions.DocumentCreationException;
import customExceptions.IncompatibleDocumentDataException;
import customExceptions.IncompatibleUserDataException;
import document.model.Document;
import document.model.DocumentType;
import model.Administrator;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentManager {

    private final Object documentsLock = new Object();
    private static final String DOCUMENTS_FILENAME = "documents.txt";
    private static final Logger logger = Logger.getLogger(DocumentManager.class.getName());

    public DocumentManager() {
       initDocuments();
    }

    private void initDocuments() {
        if (new File(DOCUMENTS_FILENAME).exists()) {
            return;
        }
        List<Document> documents = new ArrayList<>();
        Document document = new Document("CExamples", "C code and examples.", 1, DocumentType.TXT);
        documents.add(document);
        saveDocuments(documents);
        DocumentCreator documentCreator = new DocumentCreator();
        documentCreator.createNewDocuments("Adding test docs. It doesn't matter what this is right now.", 1);
    }


    public Document addDocument(String title, String description, int authorId, DocumentType documentType) throws DocumentCreationException {
        //add try and exception?
        Document document = createDocument(title, description, authorId, documentType);
        synchronized (documentsLock) {
            List<Document> documents = loadDocuments();
            documents.add(document);
            //documentManager.addDocument(document); //for a local copy of all documents in a map with id? or just map
            saveDocuments(documents);
        }
        return document;
    }

    private static Document createDocument(String title, String description, int authorId, DocumentType documentType) throws DocumentCreationException {
        //add checks for null etc.
        Document document = new Document(title, description, authorId, documentType);
        return document;
        /*switch (documentType) {

            case TXT: {
                return new Administrator(userName, password);
            }
            case JSON: {
                return new D(userName, password);
            }
            case XML: {
                return new Reviewer(userName, password);
            }
            case DOC: {
                return new Reader(userName, password);
            }
            case DOCX: {
                return new Reader(userName, password);
            }

            default:
                return null;
        }

         */
        //return null;
    }

    private List<Document> checkIfObjectIsValid(Object obj){
        List<?> tempList = (List<?>) obj;
        if (obj instanceof List<?>) {

            for (Object item : tempList) {
                if (!(item instanceof Document)) {
                    throw new ClassCastException("List contains non-Document elements");
                }
            }

        }
        return (List<Document>) tempList;
    }



    private List<Document> loadDocuments() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DOCUMENTS_FILENAME))) {
            Object obj = in.readObject();
            List<Document> documentsList = checkIfObjectIsValid(obj); //it is not needed, but it makes it more readable?

//            Map<Integer, Document> documentMap = new HashMap<>();
//            for (Document document : documentList) {
//                documentMap.put(document.getDocumentId(), document);
//            }
//            documentService.setDocuments(documentMap);

            //documentService.setDocuments((Map<String, Document>) in.readObject());
            return documentsList;
        }
        catch (IOException e) {
            if (e instanceof InvalidClassException) {
                //see if it is possible to happen when saving?
                try {
                    throw new IncompatibleDocumentDataException("One or more of the Document subclasses has likely changed." +
                            " Serializable versions are not supported." +
                            " Recreate the documents file.", e);
                } catch (IncompatibleDocumentDataException ex) {
                    logger.log(Level.SEVERE, "Error occurred", e);
                }
            }
        }
        catch (ClassNotFoundException e)
        {
            // should never happen
            throw new IllegalStateException(e);
        }

        return null;
    }

    public void saveDocuments(List<Document> documents) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DOCUMENTS_FILENAME))) {
            out.writeObject(documents);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        }
    }


}
