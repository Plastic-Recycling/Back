package recycling.back.user.register.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import recycling.back.user.register.dto.RegisterUser;
import recycling.back.user.register.service.RegisterService;
import recycling.back.util.ResponseUtil;

import java.util.Map;

@RestController
@RequestMapping("/register")
@Tag(name = "register", description = "register API")
public class RegisterController {
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/verifyEmail")
    @Operation(summary = "verify Email", description = "이메일 중복을 확인하고 해당 이메일로 인증 주소를 보낸다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam(value = "email") String email) {
        boolean isNewEmail = registerService.verifyEmail(email);
        String message;
        if(isNewEmail){
            message = "이메일 확인 메일이 전송되었습니다.";
        }else{
            message = "이미 인증된 이메일입니다. 토큰을 입력해주세요.";
        }
        return ResponseUtil.ok(isNewEmail, message);
    }
    
    @GetMapping("/confirm")
    @Operation(summary = "verify Email Confirm", description = "캐시에 저장된 정보와 일치여부 확인하고 가입용 정보 캐시에 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public ResponseEntity<String> verifyEmailConfirm(@RequestParam(value = "email") String email,
                                                         @RequestParam(value = "token") String token) {
        String url = registerService.confirmEmail(email, token);
        return ResponseUtil.found(url);
    }

    @PostMapping("/complete")
    @Operation(summary = "complete registration", description = "인증한 메일로 가입 시 회원정보 저장 후 가입 완료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 완료"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public ResponseEntity<String> completeRegistration(@RequestBody RegisterUser registerUser) {
        registerService.completeRegistration(registerUser);
        return ResponseUtil.ok("가입 완료");
    }

}
