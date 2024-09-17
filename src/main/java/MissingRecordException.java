public class MissingRecordException extends Exception {
    public MissingRecordException() {
        super();
    }

    public MissingRecordException(String message) {
        super(message);
    }

    public MissingRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingRecordException(Throwable cause) {
        super(cause);
    }

    protected MissingRecordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
