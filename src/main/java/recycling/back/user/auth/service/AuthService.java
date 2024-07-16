package recycling.back.user.auth.service;

import recycling.back.jwt.response.JwtAuthResponse;
import recycling.back.user.auth.dto.Login;

public interface AuthService {
    JwtAuthResponse login(Login login);
    JwtAuthResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
}
