package recycling.back.recycle.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmountPlastic(String result, Map<String, BigDecimal> rates) {
}
