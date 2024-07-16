package recycling.back.exception;

public class DifferentCachedInfoException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "인증 정보 불일치";

    public DifferentCachedInfoException() {
        super(DEFAULT_MESSAGE);
    }

    public DifferentCachedInfoException(String message) {
        super(message);
    }
}
