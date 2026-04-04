package document.model;
import java.time.LocalDateTime;

public class DocumentVersion {



    private final String authorId;
    private final int versionNumber;
    private final LocalDateTime createdAt;
    private final String content;
    private DocumentVersionStatus status;


    public DocumentVersion(int versionNumber, String content, String authorId) {
        this.authorId = authorId;
        this.versionNumber = versionNumber;
        this.createdAt = LocalDateTime.now(); //now for when initialized?
        this.content = content;
        //this.status = DocumentVersionStatus. //type;
    }

    public int getVersionNumber() {
        return versionNumber;
    }
    public String getContent() {
        return content;
    }
    public String getAuthorId() {
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
