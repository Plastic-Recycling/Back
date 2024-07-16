package recycling.back.recycle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recycling.back.recycle.dto.RecycleTotalCount;
import recycling.back.recycle.util.PlasticCal;
import recycling.back.recycle.repository.RecycleCountRepository;
import recycling.back.recycle.entity.RecycleCount;
import recycling.back.user.register.entity.User;
import recycling.back.user.register.repository.UserRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class RecycleService {

    private final PlasticCal plasticCal;
    private final UserRepository userRepository;
    private final RecycleCountRepository recycleCountRepository;

    @Autowired
    public RecycleService(UserRepository userRepository, PlasticCal plasticCal, RecycleCountRepository recycleCountRepository) {
        this.userRepository = userRepository;
        this.plasticCal = plasticCal;
        this.recycleCountRepository = recycleCountRepository;
    }

    public void count(String category, String name){
        User user = findUserByUsername(name);
        countUp(category, user);
    }

    private void countUp(String category, User user){
        RecycleCount recycleCount = user.getRecycleCount();
        switch (category) {
            case "pp" -> recycleCount.ppCountUp();
            case "ps" -> recycleCount.psCountUp();
            case "pe" -> recycleCount.peCountUp();
        }
        recycleCountRepository.save(recycleCount);
    }

    public Map<String, BigDecimal> allCount(){
        return allCountCal();
    }

    private Map<String, BigDecimal> allCountCal(){
        RecycleTotalCount recycleTotalCount = recycleCountRepository.getTotalRecycleCount();

        BigDecimal totalPPCount = new BigDecimal(recycleTotalCount.getTotalPP());
        BigDecimal totalPSCount = new BigDecimal(recycleTotalCount.getTotalPS());
        BigDecimal totalPECount = new BigDecimal(recycleTotalCount.getTotalPE());

        return countAndRateMap(totalPPCount, totalPSCount, totalPECount);
    }

    public Map<String, BigDecimal> rate(String name){
        User user = findUserByUsername(name);
        return userCount(user);
    }

    private Map<String, BigDecimal> userCount(User user){
        RecycleCount count = user.getRecycleCount();

        BigDecimal PPCount = new BigDecimal(count.getPp());
        BigDecimal PSCount = new BigDecimal(count.getPs());
        BigDecimal PECount = new BigDecimal(count.getPe());

        return countAndRateMap(PPCount, PSCount, PECount);
    }

    private Map<String, BigDecimal> countAndRateMap(BigDecimal PPCount, BigDecimal PSCount, BigDecimal PECount) {
        BigDecimal PPRate = PPCount.multiply(plasticCal.getRecyclePP());
        BigDecimal PSRate = PSCount.multiply(plasticCal.getRecyclePS());
        BigDecimal PERate = PECount.multiply(plasticCal.getRecyclePE());

        Map<String, BigDecimal> rating = new HashMap<>();
        rating.put("PPCount", PPCount);
        rating.put("PSCount", PSCount);
        rating.put("PECount", PECount);
        rating.put("PPRate", PPRate);
        rating.put("PSRate", PSRate);
        rating.put("PERate", PERate);
        return rating;
    }

    private User findUserByUsername(String name){
        return userRepository.findByUsername(name).orElseThrow();
    }
}
