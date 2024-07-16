package recycling.back.exception;

public class EmailDuplicateException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "이미 존재하는 이메일입니다.";

    public EmailDuplicateException() {
        super(DEFAULT_MESSAGE);
    }

    public EmailDuplicateException(String message) {
        super(message);
    }
}
