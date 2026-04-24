package test;

import model.Comment;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentTest {

    @Test
    @Order(1)
    void constructor_storesReviewerId() {
        Comment c = new Comment(42, "Nice work");
        assertEquals(42, c.getReviewerId());
    }

    @Test
    @Order(2)
    void constructor_storesContent() {
        Comment c = new Comment(1, "Please revise section 3.");
        assertEquals("Please revise section 3.", c.getContent());
    }

    @Test
    @Order(3)
    void constructor_setsCreatedAtToNow() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Comment c = new Comment(1, "time check");
        LocalDateTime after  = LocalDateTime.now().plusSeconds(1);

        assertTrue(c.getCreatedAt().isAfter(before));
        assertTrue(c.getCreatedAt().isBefore(after));
    }

    @Test
    @Order(4)
    void twoComments_haveSameReviewerButDifferentContent() {
        Comment c1 = new Comment(5, "First comment");
        Comment c2 = new Comment(5, "Second comment");

        assertEquals(c1.getReviewerId(), c2.getReviewerId());
        assertNotEquals(c1.getContent(), c2.getContent());
    }
}