package recycling.back.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

public class ResponseUtil {
    public static ResponseEntity<String> ok(String message) {
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    public static ResponseEntity<String> found(String url) {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }

    public static ResponseEntity<Map<String, BigDecimal>> ok(Map<String, BigDecimal> rate){
        return ResponseEntity.status(HttpStatus.OK).body(rate);
    }

}
