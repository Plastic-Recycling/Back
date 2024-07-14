package recycling.back.recycle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recycling.back.global.util.ResponseUtil;
import recycling.back.recycle.service.RecycleService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/recycle")
public class RecycleController {

    private final RecycleService recycleService;

    @Autowired
    public RecycleController(RecycleService recycleService) {
        this.recycleService = recycleService;
    }

    @PostMapping("/count")
    public void count(String category, Authentication authentication){
        recycleService.count(category, authentication.getName());
    }

    @GetMapping("/allCount")
    public ResponseEntity<Map<String, BigDecimal>> allCount(){
        Map<String, BigDecimal> rating = recycleService.allCount();

        return ResponseUtil.ok(rating);
    }

    @PostMapping("/rate")
    public ResponseEntity<Map<String, BigDecimal>> rate(Authentication authentication){
        Map<String, BigDecimal> rating = recycleService.rate(authentication.getName());

        return ResponseUtil.ok(rating);
    }
}
