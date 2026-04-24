package test;

import model.Document;
import model.DocumentType;
import model.DocumentVersion;
import model.DocumentVersionStatus;
import service.DocumentManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentManagerLogicTest {

    private DocumentManager manager;
    private Document doc;

    @BeforeEach
    void setUp() {
        manager = new DocumentManager();
        Document.setCounter(1);
        doc = new Document("TestDoc", "desc", 1, DocumentType.TXT);
    }
    private DocumentVersion addDraft(String content) {
        return doc.createNewVersion(content, 1);
    }


    @Test
    @Order(1)
    void approveVersion_setsStatusToApproved() {
        DocumentVersion v = addDraft("to approve");
        manager.approveVersion(v, "reviewer1");
        assertEquals(DocumentVersionStatus.APPROVED, v.getStatus());
    }

    @Test
    @Order(2)
    void approveVersion_throwsIllegalStateException_whenNotDraft() {
        DocumentVersion v = addDraft("already approved");
        manager.approveVersion(v, "reviewer1");

        //approving approved version must throw an error.
        assertThrows(IllegalStateException.class,
                () -> manager.approveVersion(v, "reviewer1"));
    }


    @Test
    @Order(3)
    void rejectVersion_setsStatusToRejected() {
        DocumentVersion v = addDraft("to reject");
        manager.rejectVersion(v, "reviewer2");
        assertEquals(DocumentVersionStatus.REJECTED, v.getStatus());
    }

    @Test
    @Order(4)
    void rejectVersion_throwsIllegalStateException_whenNotDraft() {
        DocumentVersion v = addDraft("already rejected");
        manager.rejectVersion(v, "reviewer2");

        //rejecting must throw exception.
        assertThrows(IllegalStateException.class,
                () -> manager.rejectVersion(v, "reviewer2"));
    }


    @Test
    @Order(5)
    void activateVersion_setsStatusToActive_whenApproved() {
        DocumentVersion v = addDraft("to activate");
        manager.approveVersion(v, "reviewer1");
        manager.activateVersion(doc, v);
        assertEquals(DocumentVersionStatus.ACTIVE, v.getStatus());
    }

    @Test
    @Order(6)
    void activateVersion_deactivatesPreviousActiveVersion() {

        DocumentVersion v1 = addDraft("draft1");
        DocumentVersion v2 = addDraft("drafty2");

        manager.approveVersion(v1, "v1");
        manager.approveVersion(v2, "v2");
        manager.activateVersion(doc, v1);

        manager.activateVersion(doc, v2);

        assertEquals(DocumentVersionStatus.APPROVED, v1.getStatus());
        assertEquals(DocumentVersionStatus.ACTIVE,   v2.getStatus());
    }

    @Test
    @Order(7)
    void activateVersion_throwsIllegalStateException_whenNotApproved() {
        DocumentVersion v = addDraft("this is still a draft");

        assertThrows(IllegalStateException.class,
                () -> manager.activateVersion(doc, v));
    }

    @Test
    @Order(8)
    void activateVersion_throwsIllegalStateException_forRejectedVersion() {
        DocumentVersion v = addDraft("rejected");
        manager.rejectVersion(v, "reviewer3");

        //approving rejected version.
        assertThrows(IllegalStateException.class,
                () -> manager.activateVersion(doc, v));
    }
}
