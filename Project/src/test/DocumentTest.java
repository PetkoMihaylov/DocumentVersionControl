package test;

import model.Document;
import model.DocumentType;
import model.DocumentVersion;
import model.DocumentVersionStatus;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentTest {

    @BeforeEach
    void resetCounter() {
        Document.setCounter(1);
    }


    @Test
    @Order(1)
    void constructor_setsAllFieldsCorrectly() {
        Document doc = new Document("Title", "Desc", 42, DocumentType.TXT);

        assertEquals("Title",      doc.getTitle());
        assertEquals("Desc",       doc.getDescription());
        assertEquals(42,           doc.getAuthorId());
        assertEquals("TXT",        doc.getDocumentType());
        assertEquals(1,            doc.getDocumentId());
    }

    @Test
    @Order(2)
    void documentId_autoIncrements_acrossMultipleInstances() {
        Document doc1 = new Document("A", "a", 1, DocumentType.JSON);
        Document doc2 = new Document("B", "b", 1, DocumentType.JSON);

        assertEquals(doc1.getDocumentId() + 1, doc2.getDocumentId());
    }

    @Test
    @Order(3)
    void getId_andGetDocumentId_returnSameValue() {
        Document doc = new Document("X", "x", 5, DocumentType.XML);

        assertEquals(doc.getDocumentId(), doc.getId());
    }

    @Test
    @Order(4)
    void getDocumentById_returnsSelf_whenIdMatches() {
        Document doc = new Document("Match", "m", 1, DocumentType.TXT);
        int id = doc.getDocumentId();

        assertSame(doc, doc.getDocumentById(id));
    }

    @Test
    @Order(5)
    void getDocumentById_returnsNull_whenIdDoesNotMatch() {
        Document doc = new Document("NoMatch", "n", 1, DocumentType.TXT);

        assertNull(doc.getDocumentById(doc.getDocumentId() + 99));
    }


    @Test
    @Order(6)
    void versionsIsEmpty_trueOnFreshDocument() {
        Document doc = new Document("Empty", "e", 1, DocumentType.TXT);

        assertTrue(doc.versionsIsEmpty());
    }

    @Test
    @Order(7)
    void createNewVersion_addsVersionAndReturnsDraft() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);

        DocumentVersion v = doc.createNewVersion("Hello", 1);

        assertNotNull(v);
        assertEquals(DocumentVersionStatus.DRAFT, v.getStatus());
        assertFalse(doc.versionsIsEmpty());
    }

    @Test
    @Order(8)
    void createNewVersion_assignsSequentialVersionNumbers() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);

        DocumentVersion v1 = doc.createNewVersion("first",  1);
        DocumentVersion v2 = doc.createNewVersion("second", 1);
        DocumentVersion v3 = doc.createNewVersion("third",  1);

        assertEquals(1, v1.getVersionNumber());
        assertEquals(2, v2.getVersionNumber());
        assertEquals(3, v3.getVersionNumber());
    }


    @Test
    @Order(9)
    void getLatestVersion_returnsNull_whenNoVersionsExist() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);

        assertNull(doc.getLatestVersion());
    }

    @Test
    @Order(10)
    void getLatestVersion_returnsLastCreatedVersion() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);
        doc.createNewVersion("v1", 1);
        DocumentVersion last = doc.createNewVersion("v2", 1);

        assertSame(last, doc.getLatestVersion());
    }


    @Test
    @Order(11)
    void getVersion_returnsCorrectVersionByNumber() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);
        doc.createNewVersion("first",  1);
        doc.createNewVersion("second", 1);

        DocumentVersion v = doc.getVersion(1);
        assertEquals("first", v.getContent());
    }

    @Test
    @Order(12)
    void getVersion_returnsNull_forNonExistentVersionNumber() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);
        doc.createNewVersion("only", 1);

        assertNull(doc.getVersion(99));
    }


    @Test
    @Order(13)
    void getVersionContent_returnsCorrectContent() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);
        doc.createNewVersion("alpha", 1);
        doc.createNewVersion("beta",  1);

        assertEquals("beta", doc.getVersionContent(2));
    }

    @Test
    @Order(14)
    void getVersionContent_returnsNull_forMissingVersion() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);

        assertNull(doc.getVersionContent(5));
    }


    @Test
    @Order(15)
    void getActiveVersion_returnsNull_whenNoVersionIsActive() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);
        doc.createNewVersion("draft", 1);

        assertNull(doc.getActiveVersion());
    }

    @Test
    @Order(16)
    void getActiveVersion_returnsVersionAfterStatusIsSetToActive() {
        Document doc = new Document("Doc", "d", 1, DocumentType.TXT);
        DocumentVersion v = doc.createNewVersion("content", 1);
        v.setStatus(DocumentVersionStatus.ACTIVE);

        assertSame(v, doc.getActiveVersion());
    }
}
