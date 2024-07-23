package recycling.back.recycle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import recycling.back.jwt.service.CustomUserDetails;
import recycling.back.recycle.dto.DetectionResult;
import recycling.back.recycle.entity.MountPlastic;
import recycling.back.recycle.entity.RecycleCount;
import recycling.back.recycle.repository.MountPlasticRepository;
import recycling.back.recycle.repository.RecycleCountRepository;
import recycling.back.recycle.util.ApiAmountPlastic;
import recycling.back.recycle.util.PlasticCal;
import recycling.back.user.register.entity.User;
import recycling.back.user.register.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RecycleService {

    private final PlasticCal plasticCal;
    private final UserRepository userRepository;
    private final RecycleCountRepository recycleCountRepository;
    private final RestTemplate restTemplate;
    private final ApiAmountPlastic apiAmountPlastic;
    private final MountPlasticRepository mountPlasticRepository;

    @Autowired
    public RecycleService(UserRepository userRepository, PlasticCal plasticCal,
                          RecycleCountRepository recycleCountRepository, RestTemplate restTemplate,
                          ApiAmountPlastic apiAmountPlastic, MountPlasticRepository mountPlasticRepository) {
        this.userRepository = userRepository;
        this.plasticCal = plasticCal;
        this.recycleCountRepository = recycleCountRepository;
        this.restTemplate = restTemplate;
        this.apiAmountPlastic = apiAmountPlastic;
        this.mountPlasticRepository = mountPlasticRepository;
    }

    public Map<String, List<Map<String, Object>>> profile(Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        User user = findUserByUsername(username);
        RecycleCount recycleCount = user.getRecycleCount();
        return profileDetails(recycleCount);
    }

    private Map<String, List<Map<String, Object>>> profileDetails(RecycleCount recycleCount){
        Map<String, List<Map<String, Object>>> response = new HashMap<>();

        response.put("plasticCounts", List.of(
                Map.of("name", "PP", "count", nullToZero(recycleCount.getPp(),0)),
                Map.of("name", "PS", "count", nullToZero(recycleCount.getPs(),0)),
                Map.of("name", "PE", "count", nullToZero(recycleCount.getPe(),0))
        ));

        response.put("prices", List.of(
                Map.of("name", "PP", "price", nullToZero(recycleCount.getPpRate(),0)),
                Map.of("name", "PS", "price", nullToZero(recycleCount.getPsRate(),0)),
                Map.of("name", "PE", "price", nullToZero(recycleCount.getPeRate(),0))
        ));

        response.put("weights", List.of(
                Map.of("name", "PP", "weight", nullToZero(recycleCount.getPpWeight(),0)),
                Map.of("name", "PS", "weight", nullToZero(recycleCount.getPsWeight(),0)),
                Map.of("name", "PE", "weight", nullToZero(recycleCount.getPeWeight(),0))
        ));

        response.put("carbonReductions", List.of(
                Map.of("name", "PP", "reduction", nullToZero(recycleCount.getPpCO2(),0)),
                Map.of("name", "PS", "reduction", nullToZero(recycleCount.getPsCO2(),0)),
                Map.of("name", "PE", "reduction", nullToZero(recycleCount.getPeCO2(),0))
        ));

        return response;
    }

    private static <T extends Number> T nullToZero(T value, T zero) {
        return Optional.ofNullable(value).orElse(zero);
    }

    public List<Map<String, Object>> detection(MultipartFile[] images){
        String flaskUrl = "http://localhost:5000/predict";
        MountPlastic mountPlastic = getAmountPlastic();
        List<DetectionResult> results = flaskResponse(images, flaskUrl);
        List<Map<String, Object>> responseResults = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            DetectionResult result = results.get(i);
            MultipartFile image = images[i];

            List<BigDecimal> plastic = recycleCountUp(result.getLabel(), result.getEstimateWeight(), mountPlastic);

            Map<String, Object> responseResult = new HashMap<>();
            responseResult.put("processedImage", result.getProcessedImage());
            responseResult.put("type", result.getLabel());
            responseResult.put("weight", result.getEstimateWeight());
            responseResult.put("carbonReduction", plastic.get(1));
            responseResult.put("price", plastic.get(0));
            responseResult.put("fileName", image.getOriginalFilename());
            responseResult.put("fileType", image.getContentType());
            responseResult.put("fileSize", image.getSize());

            responseResults.add(responseResult);
        }

        return responseResults;
    }

    public List<Map<String, Object>> detection(MultipartFile[] images, User user){
        String flaskUrl = "http://localhost:5000/predict";
        MountPlastic mountPlastic = getAmountPlastic();
        List<DetectionResult> results = flaskResponse(images, flaskUrl);
        List<Map<String, Object>> responseResults = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            DetectionResult result = results.get(i);
            MultipartFile image = images[i];

            List<BigDecimal> plastic = recycleCountUp(result.getLabel(), result.getEstimateWeight(),user, mountPlastic);

            Map<String, Object> responseResult = new HashMap<>();
            responseResult.put("processedImage", result.getProcessedImage()); // 나중에 반환 이미지
            responseResult.put("type", result.getLabel());
            responseResult.put("weight", result.getEstimateWeight());
            responseResult.put("carbonReduction", plastic.get(1));
            responseResult.put("price", plastic.get(0));
            responseResult.put("fileName", image.getOriginalFilename());
            responseResult.put("fileType", image.getContentType());
            responseResult.put("fileSize", image.getSize());

            responseResults.add(responseResult);
        }

        return responseResults;
    }

    private List<DetectionResult> flaskResponse(MultipartFile[] images, String flaskUrl){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile image : images){
            body.add("files[]", image.getResource());
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<List> flaskResponse = restTemplate.exchange(
                flaskUrl,
                HttpMethod.POST,
                requestEntity,
                List.class
        );

        List<Map<String, Object>> responseBody = flaskResponse.getBody();
        List<DetectionResult> results = new ArrayList<>();

        for (Map<String, Object> item : responseBody) {
            byte[] processedImage = (byte[]) item.get("processedImage");
            String label = (String) ((Map<String, Object>) ((List) item.get("shapes")).get(0)).get("label");
            double estimatedWeight = ((Number) item.get("estimatedWeight")).doubleValue();

            results.add(new DetectionResult(label, (int)estimatedWeight, processedImage));
        }

        return results;

    }

    private List<BigDecimal> recycleCountUp(String category, int weight, MountPlastic mountPlastic){
        BigDecimal plasticWeight = new BigDecimal(weight);
        BigDecimal plasticRate;
        BigDecimal plasticCO2;

        switch (category) {
            case "PP" -> {
                plasticRate = mountPlastic.getPp().multiply(plasticWeight);
                plasticCO2 = plasticCal.getRecyclePP().multiply(plasticWeight);
            }
            case "PS" -> {
                plasticRate = mountPlastic.getPs().multiply(plasticWeight);
                plasticCO2 = plasticCal.getRecyclePS().multiply(plasticWeight);
            }
            case "PE" -> {
                plasticRate = mountPlastic.getPe().multiply(plasticWeight);
                plasticCO2 = plasticCal.getRecyclePE().multiply(plasticWeight);
            }
            default -> throw new IllegalStateException("Unexpected value: " + category);
        }

        List<BigDecimal> plastic = new ArrayList<>();
        plastic.add(plasticRate);
        plastic.add(plasticCO2);
        return plastic;
    }

    private List<BigDecimal> recycleCountUp(String category, int weight, User user, MountPlastic mountPlastic){
        RecycleCount recycleCount = user.getRecycleCount();
        BigDecimal plasticWeight = new BigDecimal(weight);
        BigDecimal plasticRate;
        BigDecimal plasticCO2;
        switch (category) {
            case "PP" -> {
                plasticRate = mountPlastic.getPp().multiply(plasticWeight);
                plasticCO2 = plasticCal.getRecyclePP().multiply(plasticWeight);
                recycleCount.ppCountUp();
                recycleCount.ppWeightUp(weight);
                recycleCount.ppRateUp(plasticRate);
                recycleCount.ppCO2Up(plasticCO2);
            }
            case "PS" -> {
                plasticRate = mountPlastic.getPs().multiply(plasticWeight);
                plasticCO2 = plasticCal.getRecyclePS().multiply(plasticWeight);
                recycleCount.psCountUp();
                recycleCount.psWeightUp(weight);
                recycleCount.psRateUp(plasticRate);
                recycleCount.psCO2Up(plasticCO2);
            }
            case "PE" -> {
                plasticRate = mountPlastic.getPe().multiply(plasticWeight);
                plasticCO2 = plasticCal.getRecyclePE().multiply(plasticWeight);
                recycleCount.peCountUp();
                recycleCount.peWeightUp(weight);
                recycleCount.peRateUp(plasticRate);
                recycleCount.peCO2Up(plasticCO2);
            }
            default -> throw new IllegalStateException("Unexpected value: " + category);
        }
        recycleCountRepository.save(recycleCount);

        List<BigDecimal> plastic = new ArrayList<>();
        plastic.add(plasticRate);
        plastic.add(plasticCO2);
        return plastic;
    }

    private MountPlastic getAmountPlastic() {
        if(mountPlasticRepository.findByUpdateDateYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue()).isEmpty()){
            BigDecimal pp = apiAmountPlastic.getAmountPlastic("플레이크 (PP)");
            BigDecimal pe = apiAmountPlastic.getAmountPlastic("플레이크 (PE)");
            BigDecimal ps = apiAmountPlastic.getAmountPlastic("플레이크 (PS)");
            MountPlastic mountPlastic = MountPlastic.update(pp, pe, ps);
            mountPlasticRepository.save(mountPlastic);
        }
        return mountPlasticRepository.findByUpdateDateYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue()).orElseThrow();
    }

    private User findUserByUsername(String name){
        return userRepository.findByUsername(name).orElseThrow(
                () -> new UsernameNotFoundException("없음"));
    }
}
