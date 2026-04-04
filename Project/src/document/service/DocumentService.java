package document.service;

import document.model.Document;
import document.model.DocumentVersion;
import document.model.DocumentVersionStatus;

public class DocumentService {

    public void approveVersion(DocumentVersion version, String reviewerId) {
        if (version.getStatus() != DocumentVersionStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT versions can be approved.");
        }

        version.setStatus(DocumentVersionStatus.APPROVED);
    }

    public void rejectVersion(DocumentVersion version, String reviewerId) {
        if (version.getStatus() != DocumentVersionStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT versions can be rejected.");
        }

        version.setStatus(DocumentVersionStatus.REJECTED);
    }

    public void activateVersion(Document doc, DocumentVersion version) {
        if (version.getStatus() != DocumentVersionStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED versions can be activated.");
        }

        // deactivate current active version
        DocumentVersion current = doc.getActiveVersion();
        if (current != null) current.setStatus(DocumentVersionStatus.APPROVED);

        // activate new version
        version.setStatus(DocumentVersionStatus.ACTIVE);
    }
}