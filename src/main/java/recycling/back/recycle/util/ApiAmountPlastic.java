package recycling.back.recycle.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class ApiAmountPlastic {

    @Value("${api.userid}")
    private String apiUserId;

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ApiAmountPlastic(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public BigDecimal getAmountPlastic(String materialName) {
        String url = String.format("https://www.recycling-info.or.kr/sds/JsonApi.do?PID=MART01&YEAR=%d&USRID=%s&KEY=%s",
                LocalDate.now().getYear(), apiUserId, apiKey);

        String response = restTemplate.getForObject(url, String.class);
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode dataNode = rootNode.path("data");
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue() - 1;
        String currentYearMonth = String.format("%d.%02d", year, month);

        for (JsonNode node : dataNode) {
            if (node.path("MATERIAL_NAME").asText().equals(materialName)
                    && node.path("INPUT_YYMM").asText().equals(currentYearMonth)) {
                BigDecimal value = node.path("ALL_AVG").decimalValue();
                return value.divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
            }
        }

        throw new IllegalArgumentException("올바르지 않은 정보: " + currentYearMonth);
    }
}