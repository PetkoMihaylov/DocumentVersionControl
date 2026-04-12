package document.service;

import document.model.Document;
import document.model.DocumentType;
import document.model.DocumentVersion;
import document.model.DocumentVersionStatus;
import model.Administrator;
import model.Author;
import model.Reader;
import model.Reviewer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class DocumentService {

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