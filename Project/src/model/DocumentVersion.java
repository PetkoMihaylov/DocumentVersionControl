package model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentVersion implements Serializable {
    private final int authorId;
    private int versionNumber;
    private final LocalDateTime createdAt;
    private String content;
    private DocumentVersionStatus status;

    private List<Comment> commentsByReviewer; //comments on versions by reviewer?
    private String reviewedAt;

    public DocumentVersion(int versionNumber, String content, int authorId) {
        this.authorId = authorId;
        this.versionNumber = versionNumber;
        this.createdAt = LocalDateTime.now(); //now for when it's initialized?
        this.content = content;
        this.status = DocumentVersionStatus.DRAFT; //default type;

        this.commentsByReviewer = new ArrayList<>();
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
        System.out.println("AuthorId: " + authorId);
        System.out.println("VersionNumber: " + versionNumber);
        System.out.println("CreatedAt: " + createdAt);
        System.out.println("Content: " + content);
        System.out.println("Status: " + status);
    }

    public void setContent(int versionNumber, String newContent, int userId) {

        content = newContent;
    }

    public void addComment(int reviewerId, String comment){
        Comment com = new Comment(reviewerId, comment);
        commentsByReviewer.add(reviewerId, com);
    }

    public List<Comment> getComments() {
        return commentsByReviewer;
    }
}


//    public void setContent(String content) {
    //because it is final, cannot change history, but is this restriction by the project description?
//        this.content = content;
//    }

