package manager;

enum AuthorActions {
    CREATE_DOCUMENT,
    CREATE_VERSION,
    LIST_DOCUMENTS, //lists all documents
    LIST_VERSIONS_INFO, //returns only info
    LIST_DRAFTS, //lists all draft versions
    VIEW_DRAFT, //command send to view a specific draft version
    EDIT_DRAFT, //command send to edit a specific draft version
    VIEW_DOCUMENT_HISTORY //returns document history of changes?
}
