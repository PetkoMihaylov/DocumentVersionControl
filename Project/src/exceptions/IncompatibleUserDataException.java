package exceptions;

import java.io.InvalidClassException;

public class IncompatibleUserDataException extends InvalidClassException {

    public IncompatibleUserDataException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
