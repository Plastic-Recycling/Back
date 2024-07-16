package recycling.back.exception;

public class NotInitialEmailException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "인증한 메일과 다른 이메일입니다.";

    public NotInitialEmailException() {
        super(DEFAULT_MESSAGE);
    }

    public NotInitialEmailException(String message) {
        super(message);
    }
}
