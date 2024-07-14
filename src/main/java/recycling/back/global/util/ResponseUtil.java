package recycling.back.global.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    public static ResponseEntity<String> ok(String message) {
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    public static ResponseEntity<Map<String, String>> ok(String key, String value) {
        Map<String, String> response = new HashMap<>();
        response.put(key, value);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public static ResponseEntity<Map<String, BigDecimal>> ok(Map<String, BigDecimal> rate){
        return ResponseEntity.status(HttpStatus.OK).body(rate);
    }

}
