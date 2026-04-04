package document.model;
import java.util.ArrayList;
import java.util.List;

public class Document {
    private final int documentId;
    private String title;
    private String description;
    private int authorId;
    private DocumentType documentType;

    private final List<DocumentVersion> versions = new ArrayList<>();

    public Document(int documentId, String title, String description, int authorId, DocumentType documentType) {
        this.documentId = documentId;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.documentType = documentType;
    }

    public int getDocumentId() {
        return documentId;
    }

    public int getAuthorId () {
        return authorId;
    }
    public String getDocumentType() {
        return documentType.toString();
    }

    public DocumentVersion createNewVersion(String content, int userId) {
        int newVersionNumber = versions.size() + 1;

        DocumentVersion version = new DocumentVersion(newVersionNumber, content, userId);

        versions.add(version);
        return version;
    }

    public DocumentVersion getLatestVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.getLast();
    }

    public List<DocumentVersion> getAllVersions() {
        return versions;
    }

    public DocumentVersion getActiveVersion() {
        return versions.stream()
                .filter(v -> v.getStatus() == DocumentVersionStatus.ACTIVE)
                .findFirst().orElse(null);
    }
}