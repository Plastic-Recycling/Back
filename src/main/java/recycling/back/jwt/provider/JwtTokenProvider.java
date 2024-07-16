package recycling.back.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import recycling.back.jwt.config.JwtTokenConfig;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    private final JwtTokenConfig jwtTokenConfig;

    public JwtTokenProvider(JwtTokenConfig jwtTokenConfig) {
        this.jwtTokenConfig = jwtTokenConfig;
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, jwtTokenConfig.getAccessTokenValidityInTime());
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, jwtTokenConfig.getRefreshTokenValidityInTime());
    }

    private String createToken(Authentication authentication, long validityInTime){

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validityInTime))
                .signWith(jwtTokenConfig.getSignKey())
                .compact();
    }

    public String getUsername(String token){
        return getClaimFromToken(token, Claims::getSubject);

    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) jwtTokenConfig.getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) jwtTokenConfig.getSignKey())
                    .build()
                    .parse(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
