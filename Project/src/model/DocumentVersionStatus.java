package model;

public enum DocumentVersionStatus {
    DRAFT,
    APPROVED,
    REJECTED,
    AWAITING_APPROVAL, //currently not implemented, but ideas to be set by author and then approved? (draft is used for this))
    ACTIVE
}