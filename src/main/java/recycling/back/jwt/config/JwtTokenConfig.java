package recycling.back.jwt.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;

@Configuration
public class JwtTokenConfig {
    private final Long accessTokenValidityInTime;
    private final Long refreshTokenValidityInTime;
    private final String secretKey;

    public Long getAccessTokenValidityInTime() {
        return accessTokenValidityInTime;
    }

    public Long getRefreshTokenValidityInTime() {
        return refreshTokenValidityInTime;
    }

    public JwtTokenConfig(@Value("${jwt.access_expiration_time}") Long accessTokenValidityInTime,
                          @Value("${jwt.refresh_expiration_time}") Long refreshTokenValidityInTime,
                          @Value("${jwt.secret}") String secretKey) {
        this.accessTokenValidityInTime = accessTokenValidityInTime;
        this.refreshTokenValidityInTime = refreshTokenValidityInTime;
        this.secretKey = secretKey;
    }

    public Key getSignKey(){
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
