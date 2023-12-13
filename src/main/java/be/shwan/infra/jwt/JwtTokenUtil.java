package be.shwan.infra.jwt;


import be.shwan.infra.config.AppProperties;
import be.shwan.modules.account.domain.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 5*60*60;
    private final AppProperties appProperties;
    private static final SecretKey SECRET_KEY;

    static {
        try {
            SECRET_KEY = getSECRET_KEY();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKey getSECRET_KEY() throws NoSuchAlgorithmException {
        return Jwts.SIG.HS256.key().random(SecureRandom.getInstance("SHA1PRNG")).build();
    }

    public String generateToken(Account account){
        return Jwts.builder()
                .issuer("http://localhost:8080")
                .subject("인증된 사용자")
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .claim("username", account.getNickname())
                .signWith(SECRET_KEY).compact();
    }

    public String generateToken(String username){
        return Jwts.builder()
                .issuer("http://localhost:8080")
                .subject("인증된 사용자")
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .claim("username", username)
                .signWith(SECRET_KEY).compact();
    }

    public String parseIssuer(String token) {
        Claims claims = parsePayload(token);
        return claims.getIssuer();
    }

    public Date parseExpiration(String token) {
        Claims claims = parsePayload(token);
        return claims.getExpiration();
    }

    public String parseUsername(String token) {
        Claims claims = parsePayload(token);
        return claims.get("username", String.class);
    }


    private Claims parsePayload(String token) {
        return parseToken(token).getPayload();
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build()
                .parseSignedClaims(token);
    }

    public boolean validate(String token) {
        String issuer = parseIssuer(token);
        Date expiration = parseExpiration(token);
        return appProperties.getHost().equals(issuer) && expiration.after(new Date());
    }
}
