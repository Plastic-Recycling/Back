package recycling.back.user.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recycling.back.jwt.response.JwtAuthResponse;
import recycling.back.user.auth.dto.RefreshTokenRequest;
import recycling.back.user.auth.service.AuthService;
import recycling.back.user.auth.dto.Login;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody Login login){
        JwtAuthResponse jwtAuthResponse = authService.login(login);
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtAuthResponse jwtAuthResponse = authService.refreshToken(refreshTokenRequest.getRefreshToken());
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest.getRefreshToken());
        return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
    }

}
