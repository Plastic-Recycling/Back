package recycling.back.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import recycling.back.global.util.ResponseUtil;
import recycling.back.user.dto.RegisterUser;
import recycling.back.user.service.RegisterService;

import java.util.Map;

@RestController
@RequestMapping("/register")
public class RegisterController {
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam(value = "email") String email) {
        registerService.verifyEmail(email);
        return ResponseUtil.ok("이메일 확인");
    }
    @GetMapping("/confirm")
    public ResponseEntity<Map<String, String>> verifyEmailConfirm(@RequestParam(value = "email") String email,
                                                         @RequestParam(value = "token") String token) {
        registerService.confirmEmail(email, token);
        return ResponseUtil.ok("email", email);
    }

    @PostMapping("/complete")
    public ResponseEntity<String> completeRegistration(@RequestBody RegisterUser registerUser) {
        registerService.completeRegistration(registerUser);
        return ResponseUtil.ok("가입 완료");
    }

}
