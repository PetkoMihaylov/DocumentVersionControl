package exceptions;

import java.io.InvalidClassException;

public class IncompatibleDocumentDataException extends InvalidClassException {

    public IncompatibleDocumentDataException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
