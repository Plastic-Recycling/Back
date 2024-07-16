package recycling.back.user.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import recycling.back.jwt.provider.JwtTokenProvider;
import recycling.back.jwt.response.JwtAuthResponse;
import recycling.back.user.auth.dto.Login;
import recycling.back.user.auth.entity.RefreshToken;
import recycling.back.user.auth.repository.RefreshTokenRepository;
import recycling.back.user.register.entity.User;
import recycling.back.user.register.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                           UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public JwtAuthResponse login(Login login) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                login.getUsername(), login.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("유저 정보 없음"));

        String accessToken;
        String refreshToken;

        // 리프레시 토큰 존재 여부 확인
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(user);

        if (existingRefreshToken.isPresent() &&
                existingRefreshToken.get().isValid() &&
                jwtTokenProvider.validateToken(existingRefreshToken.get().getToken())) {
            // 기존 리프레시 토큰이 유효한 경우
            refreshToken = existingRefreshToken.get().getToken();
            // 액세스 토큰 재발급
            accessToken = jwtTokenProvider.createAccessToken(authentication);
        } else {
            // 리프레시 토큰이 없거나 유효하지 않은 경우 새로 생성
            accessToken = jwtTokenProvider.createAccessToken(authentication);
            refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            Instant refreshTokenExpiryDate = Instant.now().plus(7, ChronoUnit.DAYS);
            RefreshToken refreshTokenEntity = RefreshToken.create(user, refreshToken, refreshTokenExpiryDate);
            refreshTokenRepository.save(refreshTokenEntity);
        }

        return new JwtAuthResponse(accessToken, refreshToken);
    }

    @Override
    public JwtAuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token is not valid");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!storedToken.getUser().getUsername().equals(username) || !storedToken.isValid() || storedToken.isExpired()) {
            throw new RuntimeException("Refresh token is not valid");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        storedToken.invalidate();
        refreshTokenRepository.save(storedToken);

        Instant newRefreshTokenExpiryDate = Instant.now().plus(7, ChronoUnit.DAYS);
        RefreshToken newRefreshTokenEntity = RefreshToken.create(storedToken.getUser(), newRefreshToken, newRefreshTokenExpiryDate);
        refreshTokenRepository.save(newRefreshTokenEntity);

        return new JwtAuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        storedToken.invalidate();
        refreshTokenRepository.delete(storedToken);
    }
}
