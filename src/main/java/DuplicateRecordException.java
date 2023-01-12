public class DuplicateRecordException extends Exception {
    public DuplicateRecordException() {
        super();
    }

    public DuplicateRecordException(String message) {
        super(message);
    }

    public DuplicateRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRecordException(Throwable cause) {
        super(cause);
    }

    protected DuplicateRecordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
