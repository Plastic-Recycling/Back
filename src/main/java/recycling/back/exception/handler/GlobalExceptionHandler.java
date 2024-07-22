package recycling.back.exception.handler;

import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import recycling.back.exception.DifferentCachedInfoException;
import recycling.back.exception.EmailDuplicateException;
import recycling.back.exception.NotInitialEmailException;
import recycling.back.exception.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EmailDuplicateException.class, NotInitialEmailException.class,
            DifferentCachedInfoException.class, DuplicateRequestException.class})
    protected ResponseEntity<Object> handleRegisterException(RuntimeException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
