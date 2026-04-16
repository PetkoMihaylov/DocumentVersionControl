package document.model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentVersion implements Serializable {
    private final int authorId;
    private int versionNumber;
    private final LocalDateTime createdAt;
    private final String content;
    private DocumentVersionStatus status;


    public DocumentVersion(int versionNumber, String content, int authorId) {
        this.authorId = authorId;
        this.versionNumber = versionNumber;
        this.createdAt = LocalDateTime.now(); //now for when it's initialized?
        this.content = content;
        this.status = DocumentVersionStatus.DRAFT; //default type;
    }

    public void setCounter(int value) {

    }

    public int getVersionNumber() {
        return versionNumber;
    }
//    public int getLatestVersionNumber(){
//        return 0;
//    }

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

    public DocumentVersion getDraft() {
        if(status == DocumentVersionStatus.DRAFT) {
            return this;
        }
        return null;
    }
    public void printAllVersionData(){
        //System.out.println("Doc ID: " );
        System.out.println("authorId: " + authorId);
        System.out.println("versionNumber: " + versionNumber);
        System.out.println("createdAt: " + createdAt);
        System.out.println("content: " + content);
        System.out.println("status: " + status);
    }

//    public void setContent(String content) {
    //because it is final, cannot change history, but is this restriction by the project description?
//        this.content = content;
//    }
}
