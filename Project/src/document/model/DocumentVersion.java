package document.model;
import java.time.LocalDateTime;

public class DocumentVersion {

    private final int authorId;
    private final int versionNumber;
    private final LocalDateTime createdAt;
    private final String content;
    private DocumentVersionStatus status;


    public DocumentVersion(int versionNumber, String content, int authorId) {
        this.authorId = authorId;
        this.versionNumber = versionNumber;
        this.createdAt = LocalDateTime.now(); //now for when it's initialized?
        this.content = content;
        this.status = DocumentVersionStatus.DRAFT;//type;
    }

    public int getVersionNumber() {
        return versionNumber;
    }
    public String getContent() {
        return content;
    }
    public int getAuthorId() {
        return authorId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public DocumentVersionStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentVersionStatus status) {
        this.status = status;
    }



}
