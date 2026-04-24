package test;

import model.Comment;
import model.DocumentVersion;
import model.DocumentVersionStatus;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentVersionTest {

    private DocumentVersion makeVersion(String content) {
        return new DocumentVersion(1, content, 99);
    }

    @Test
    @Order(1)
    void constructor_setsVersionNumber() {
        DocumentVersion v = new DocumentVersion(3, "text", 1);
        assertEquals(3, v.getVersionNumber());
    }

    @Test
    @Order(2)
    void constructor_setsContent() {
        DocumentVersion v = makeVersion("Hello World");
        assertEquals("Hello World", v.getContent());
    }

    @Test
    @Order(3)
    void constructor_setsAuthorId() {
        DocumentVersion v = new DocumentVersion(1, "x", 55);
        assertEquals(55, v.getAuthorId());
    }

    @Test
    @Order(4)
    void constructor_setsStatusToDraft() {
        // every version must begin as DRAFT.
        DocumentVersion v = makeVersion("draft content");
        assertEquals(DocumentVersionStatus.DRAFT, v.getStatus());
    }

    @Test
    @Order(5)
    void constructor_setsCreatedAtToNow() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        DocumentVersion v = makeVersion("time test");
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // createdAt must be correct
        assertTrue(v.getCreatedAt().isAfter(before));
        assertTrue(v.getCreatedAt().isBefore(after));
    }

    @Test
    @Order(6)
    void constructor_initializesEmptyCommentList() {
        DocumentVersion v = makeVersion("initVersion");
        assertNotNull(v.getComments());
        assertTrue(v.getComments().isEmpty());
    }


    @Test
    @Order(7)
    void getDraft_returnsSelf_whenStatusIsDraft() {
        DocumentVersion v = makeVersion("draft");
        assertSame(v, v.getDraft());
    }

    @Test
    @Order(8)
    void getDraft_returnsNull_whenStatusIsNotDraft() {
        DocumentVersion v = makeVersion("approved");
        v.setStatus(DocumentVersionStatus.APPROVED);
        assertNull(v.getDraft());
    }

    @Test
    @Order(9)
    void setStatus_changesStatusCorrectly() {
        DocumentVersion v = makeVersion("content");
        v.setStatus(DocumentVersionStatus.APPROVED);
        assertEquals(DocumentVersionStatus.APPROVED, v.getStatus());
    }

    @Test
    @Order(10)
    void setContent_updatesContent() {
        DocumentVersion v = makeVersion("old content");
        v.setContent(1, "new content", 99);
        assertEquals("new content", v.getContent());
    }

    @Test
    @Order(11)
    void addComment_appendsCommentToList() {
        DocumentVersion v = makeVersion("reviewed");
        v.addComment(7, "Looks good");

        assertEquals(1, v.getComments().size());
        Comment c = v.getComments().get(0);
        assertEquals(7,            c.getReviewerId());
        assertEquals("Looks good", c.getContent());
    }

    @Test
    @Order(12)
    void addComment_supportsMultipleComments_withoutIndexException() {
        DocumentVersion v = makeVersion("multi-comment");

        v.addComment(100, "First");
        v.addComment(200, "Second");
        v.addComment(300, "Third");

        assertEquals(3, v.getComments().size());
    }
}
