package recycling.back.recycle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import recycling.back.jwt.service.CustomUserDetails;
import recycling.back.recycle.service.RecycleService;
import recycling.back.user.register.entity.User;
import recycling.back.user.register.repository.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recycle")
public class RecycleController {

    private final RecycleService recycleService;
    private final UserRepository userRepository;

    @Autowired
    public RecycleController(RecycleService recycleService, UserRepository userRepository) {
        this.recycleService = recycleService;
        this.userRepository = userRepository;
    }

    @PostMapping("/profile")
    public Map<String, List<Map<String, Object>>> getProfileData(Authentication authentication) {
        return recycleService.profile(authentication);
    }

    @PostMapping("/detection")
    public ResponseEntity<List<Map<String, Object>>> detection(@RequestParam("file") MultipartFile[] file, Authentication authentication) {
        String username = null;
        if(authentication != null){
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            username = customUserDetails.getUsername();

            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("없음"));

            List<Map<String, Object>> result = recycleService.detection(file, user);
            return ResponseEntity.ok(result);
        }else{
            List<Map<String, Object>> result = recycleService.detection(file);
            return ResponseEntity.ok(result);
        }
    }
}
