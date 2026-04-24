package service;

import exceptions.IncompatibleDocumentDataException;
import model.Document;
import model.DocumentType;
import model.DocumentVersion;
import model.DocumentVersionStatus;
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


    public DocumentManager() {
        initDocuments();
    }

    private void initDocuments() {
        if (new File(DOCUMENTS_FILENAME).exists()) {
            //loadDocuments();
            return;
        }

        List<Document> documents = new ArrayList<>();
        Document document = new Document("CExamples", "C code and examples.", 1, DocumentType.TXT);
        documents.add(document);
        saveDocuments(documents);
        DocumentCreator documentCreator = new DocumentCreator();
        documentCreator.createNewDocuments(new DocumentService(), "s", 1); //no matter what it's here

    }

    List<Document> loadDocumentsFromFile() {
        //add check if directory exists
        //separate files for the different documents? XML...
        //permissions folder/files

        File file = new File(DOCUMENTS_FILENAME);
        if (!file.exists()) {
            //no files present
            //System.out.print("no files");
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DOCUMENTS_FILENAME))) {
            Object obj = in.readObject();
            List<Document> documentsList = checkIfObjectIsValid(obj);

            //System.out.println("\nI am passing through load documents!\n\n");
            //System.out.println("\n\n");

            for (Document document : documentsList) {
                if (document.getAllVersions() != null && !document.getAllVersions().isEmpty()) {
                    //System.out.println("OHNO -> " + document.getAllVersions());
                }
            }

            // maxId is for syncing the current new Document ID in Document
            int maxId = 0;
            for (Document document : documentsList) {
                if (document.getDocumentId() > maxId) {
                    maxId = document.getDocumentId();
                    //System.out.println("\nPrinting MAX " + maxId + "\n");
                }
            }

            for (Document document : documentsList) {
                if (document.versionsIsEmpty()) {
                    //System.out.println("\n\n;(\n\n");
                } else {
                    document.getLatestVersion();
                }
            }

            return documentsList;
        } catch (IOException e) {
            if (e instanceof InvalidClassException) {
                //see if it is possible to happen when saving?
                try {
                    throw new IncompatibleDocumentDataException("One or more of the Document subclasses has likely changed." +
                            " Serializable versions are not supported." +
                            " Recreate the documents file.", e);
                } catch (IncompatibleDocumentDataException ex) {
                    logger.log(Level.SEVERE, "Error occurred", ex);
                }
            } else {
                logger.log(Level.SEVERE, "Error occurred while loading documents", e);
            }
        } catch (ClassNotFoundException e) {
            // should never happen
            throw new IllegalStateException(e);
        }
        return new ArrayList<>(); // read that this is preferable to returning null
    }

    public void saveDocuments(List<Document> documents) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DOCUMENTS_FILENAME))) {
            out.writeObject(documents);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        }
    }

    private List<Document> checkIfObjectIsValid(Object obj) {
        if (!(obj instanceof List<?>)) {
            throw new ClassCastException("Loaded object is not a List");
        }
        List<?> tempList = (List<?>) obj;
        for (Object item : tempList) {
            if (!(item instanceof Document)) {
                throw new ClassCastException("List contains non-Document elements");
            }
        }
        return (List<Document>) tempList;
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
