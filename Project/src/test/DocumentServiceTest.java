package test;

import model.Document;
import model.DocumentType;
import model.DocumentVersion;
import model.DocumentVersionStatus;
import service.DocumentService;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentServiceTest {

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService();
        documentService.setDocuments(new HashMap<>());
        Document.setCounter(1);
    }

    private Document makeDoc(String title) throws Exception {
        return documentService.createDocument(title, "desc", 1, DocumentType.TXT);
    }


    @Test
    @Order(1)
    void createDocument_returnsNonNullDocument() throws Exception {
        Document doc = makeDoc("My Doc");
        assertNotNull(doc);
    }

    @Test
    @Order(2)
    void createDocument_storesTitleAndType() throws Exception {
        Document doc = documentService.createDocument("API Spec", "details", 2, DocumentType.JSON);
        assertEquals("API Spec", doc.getTitle());
        assertEquals("JSON",     doc.getDocumentType());
    }

    @Test
    @Order(3)
    void createDocument_isRetrievableById() throws Exception {
        Document doc = makeDoc("Findable");
        assertSame(doc, documentService.getDocumentById(doc.getDocumentId()));
    }

    @Test
    @Order(4)
    void createDocument_appearsInGetAllDocuments() throws Exception {
        Document doc = makeDoc("Listed");
        assertTrue(documentService.getAllDocuments().contains(doc));
    }


    @Test
    @Order(5)
    void addVersionToDocument_returnsVersionWithCorrectContent() throws Exception {
        Document doc = makeDoc("Versioned");
        DocumentVersion v = documentService.addVersionToDocument(doc.getId(), "Hello", 1);

        assertNotNull(v);
        assertEquals("Hello", v.getContent());
    }

    @Test
    @Order(6)
    void addVersionToDocument_returnsNull_forUnknownDocumentId() {
        //id 42 should not exist
        DocumentVersion v = documentService.addVersionToDocument(42, "content", 1);
        assertNull(v);
    }

    @Test
    @Order(7)
    void addVersionToDocument_incrementsVersionNumbers() throws Exception {
        Document doc = makeDoc("MultiVer");
        DocumentVersion v1 = documentService.addVersionToDocument(doc.getId(), "v1", 1);
        DocumentVersion v2 = documentService.addVersionToDocument(doc.getId(), "v2", 1);

        assertEquals(1, v1.getVersionNumber());
        assertEquals(2, v2.getVersionNumber());
    }

    @Test
    @Order(8)
    void getDraftContent_returnsCorrectContent() throws Exception {
        Document doc = makeDoc("ContentDoc");
        documentService.addVersionToDocument(doc.getId(), "draft text", 1);

        assertEquals("draft text", documentService.getDraftContent(doc.getId(), 1));
    }

    @Test
    @Order(9)
    void getDraftContent_returnsNull_forNonExistentVersion() throws Exception {
        Document doc = makeDoc("EmptyDoc");
        assertNull(documentService.getDraftContent(doc.getId(), 5));
    }


    @Test
    @Order(10)
    void getDraftContentForTwoVersions_returnsBothContents() throws Exception {
        Document doc = makeDoc("TwoVer");
        documentService.addVersionToDocument(doc.getId(), "alpha", 1);
        documentService.addVersionToDocument(doc.getId(), "beta",  1);

        String[] pair = documentService.getDraftContentForTwoVersions(doc.getId(), 1, 2);
        assertEquals("alpha", pair[0]);
        assertEquals("beta",  pair[1]);
    }


    @Test
    @Order(11)
    void editDraftDocument_updatesContent_whenAuthorMatches() throws Exception {
        Document doc = makeDoc("EditDoc");
        documentService.addVersionToDocument(doc.getId(), "original", 1);

        String result = documentService.editDraftDocument(doc.getId(), 1, "updated", 1);
        assertEquals("Content was edited!", result);
        assertEquals("updated", documentService.getDraftContent(doc.getId(), 1));
    }

    @Test
    @Order(12)
    void editDraftDocument_rejectsEdit_whenAuthorDoesNotMatch() throws Exception {
        Document doc = makeDoc("WrongAuthor");
        documentService.addVersionToDocument(doc.getId(), "original", 1);

        //author id 1
        String result = documentService.editDraftDocument(doc.getId(), 1, "replaced", 99);
        assertEquals("AuthorId not correct!", result);
    }


    @Test
    @Order(13)
    void approveVersion_changesStatusToApproved() throws Exception {
        Document doc = makeDoc("ApproveDoc");
        documentService.addVersionToDocument(doc.getId(), "content", 1);

        documentService.approveVersion(doc.getId(), 1);

        assertEquals(DocumentVersionStatus.APPROVED,
                doc.getVersion(1).getStatus()); //could there be issue with indexing?
    }

    @Test
    @Order(14)
    void approveVersion_returnsError_forAlreadyApprovedVersion() throws Exception {
        Document doc = makeDoc("DoubleApprove");
        documentService.addVersionToDocument(doc.getId(), "content", 1);
        documentService.approveVersion(doc.getId(), 1);

        String result = documentService.approveVersion(doc.getId(), 1);
        assertTrue(result.startsWith("Error:"));
    }

    @Test
    @Order(15)
    void approveVersion_returnsError_forNonExistentDocument() {
        //should not approve
        String result = documentService.approveVersion(999, 1);
        assertEquals("Document does not exist!", result);
    }


    @Test
    @Order(16)
    void rejectVersion_changesStatusToRejected() throws Exception {
        Document doc = makeDoc("RejectDoc");
        documentService.addVersionToDocument(doc.getId(), "content", 1);

        documentService.rejectVersion(doc.getId(), 1);

        assertEquals(DocumentVersionStatus.REJECTED,
                doc.getVersion(1).getStatus());
    }

    @Test
    @Order(17)
    void rejectVersion_returnsError_forAlreadyRejectedVersion() throws Exception {
        Document doc = makeDoc("DoubleReject");
        documentService.addVersionToDocument(doc.getId(), "content", 1);
        documentService.rejectVersion(doc.getId(), 1);

        String result = documentService.rejectVersion(doc.getId(), 1);
        assertTrue(result.startsWith("Error:"));
    }

    @Test
    @Order(18)
    void activateVersion_changesStatusToActive_afterApproval() throws Exception {
        Document doc = makeDoc("ActivateDocument");
        documentService.addVersionToDocument(doc.getId(), "content", 1);
        documentService.approveVersion(doc.getId(), 1);

        documentService.activateVersion(doc.getId(), 1);

        assertEquals(DocumentVersionStatus.ACTIVE, doc.getVersion(1).getStatus());
    }

    @Test
    @Order(19)
    void activateVersion_deactivatesPreviousActiveVersion() throws Exception {
        Document doc = makeDoc("TwoActive");
        documentService.addVersionToDocument(doc.getId(), "v1", 1);
        documentService.addVersionToDocument(doc.getId(), "v2", 1);
        documentService.approveVersion(doc.getId(), 1);
        documentService.approveVersion(doc.getId(), 2);
        documentService.activateVersion(doc.getId(), 1);

        //activating another version makes the last active to approved, (I think?, check asap)
        documentService.activateVersion(doc.getId(), 2);

        assertEquals(DocumentVersionStatus.APPROVED, doc.getVersion(1).getStatus());
        assertEquals(DocumentVersionStatus.ACTIVE,   doc.getVersion(2).getStatus());
    }

    @Test
    @Order(20)
    void addCommentToVersion_appendsCommentAndReturnsSuccessMessage() throws Exception {
        Document doc = makeDoc("CommentDoc");
        documentService.addVersionToDocument(doc.getId(), "content", 1);

        String result = documentService.addCommentToVersion(doc.getId(), 1, 7, "Good job");

        assertTrue(result.contains("Comment added"));

        assertEquals(1, doc.getVersion(1).getComments().size());
        assertEquals("Good work", doc.getVersion(1).getComments().get(0).getContent());
    }
}
