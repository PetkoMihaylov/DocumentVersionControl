package document.service;

import customExceptions.DocumentCreationException;
import customExceptions.IncompatibleDocumentDataException;
import customExceptions.IncompatibleUserDataException;
import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;
import document.model.DocumentVersionStatus;
import model.Administrator;
import model.User;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentManager {

    private final Object documentsLock = new Object();
    private static final String DOCUMENTS_FILENAME = "documents.bin";
    private static final Logger logger = Logger.getLogger(DocumentManager.class.getName());

    private static DocumentService documentService = new DocumentService();
    //private List<Document> documents = new ArrayList<>();

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
        documentService.addDocument(document);
        saveDocuments(documents);
        DocumentCreator documentCreator = new DocumentCreator();
        documentCreator.createNewDocuments("Adding test docs. It doesn't matter what this is right now.", 1);
    }


    public Document addDocument(String title, String description, int authorId, DocumentType documentType) throws DocumentCreationException {
        //THIS method is actually the one that has to be called when creating a new document)
        //add try and exception?
        Document document = createDocument(title, description, authorId, documentType);
        synchronized (documentsLock) {
            List<Document> documents = loadDocuments();
            documents.add(document);
            documentService.addDocument(document);
            //documentManager.addDocument(document); //for a local copy of all documents in a map with id? or just map
            saveDocuments(documents);
        }
        return document;
    }

    private static Document createDocument(String title, String description, int authorId, DocumentType documentType) throws DocumentCreationException {
        //why did I separate this class createDocument again and use addDocument to call it?? FIX IF YOU HAVE TIME
        //add checks for null etc.
        Document document = new Document(title, description, authorId, documentType);
        documentService.addDocument(document); //this was not commented before 14.04.2026, does it mean duplicates were created?
        //documentService.addDocument(document);
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


    List<Document> loadDocuments() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DOCUMENTS_FILENAME))) {
            Object obj = in.readObject();
            List<Document> documentsList = checkIfObjectIsValid(obj); //it is not needed, but it makes it more readable?

            System.out.println("\nI am passing through load documents!\n\n");
            System.out.println(documentsList.getLast().getAllVersions());
            System.out.println("\n\n");

            if(!documentsList.isEmpty()){
                Map<Integer, Document> documentMap = new HashMap<>();
                for (Document document : documentsList) {
                    documentMap.put(document.getDocumentId(), document);
                }

                documentService.setDocuments(documentMap);
            }
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

    public void approveVersion(DocumentVersion version, String reviewerId) {
        if (version.getStatus() != DocumentVersionStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT versions can be approved.");
        }

        version.setStatus(DocumentVersionStatus.APPROVED);
    }

    public void rejectVersion(DocumentVersion version, String reviewerId) {
        if (version.getStatus() != DocumentVersionStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT versions can be rejected.");
        }

        version.setStatus(DocumentVersionStatus.REJECTED);
    }

    public void activateVersion(Document document, DocumentVersion version) {
        if (version.getStatus() != DocumentVersionStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED versions can be activated.");
        }

        // deactivate current active version
        DocumentVersion current = document.getActiveVersion();
        if (current != null) {
            current.setStatus(DocumentVersionStatus.APPROVED);
        }

        // activate new version
        version.setStatus(DocumentVersionStatus.ACTIVE);
    }

    public void readDocument(Document document, String userId) throws ParserConfigurationException, IOException, SAXException {
        DocumentType documentType = DocumentType.valueOf(document.getDocumentType());
        switch (documentType) {

            case TXT: {
//                // how to not be repetitive with the ifs when static doesn't work outside of this subclass?
            }
            case JSON: {

            }
            case XML: {
//                File file = new File("file.xml");
//                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//                DocumentBuilder db = dbf.newDocumentBuilder();
//                Document documentXML = db.parse(file);

            }
            case DOC: {

            }

//            default:
//                return null;
            case DOCX: {

            }
        }
        //Files.readString(Path.of("file.txt"));
    }
}
