package document;

import java.time.LocalDateTime;

public class DocumentVersion {

    public enum DocumentVersionStatus {
        DRAFT,
        APPROVED,
        REJECTED,
        ACTIVE
    }

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


}
