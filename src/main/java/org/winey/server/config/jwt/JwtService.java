package org.winey.server.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.exception.model.UnauthorizedException;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder()
                .encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 토큰 발급
    public String issuedToken(String userId, Long tokenExpirationTime) {
        final Date now = new Date();

        // 클레임 생성
        final Claims claims = Jwts.claims()
                .setSubject("token")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpirationTime));

        //private claim 등록
        claims.put("userId", userId);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE , Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        final byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 검증
    public boolean verifyToken(String token) {
        try {
            final Claims claims = getBody(token);
            return true;
        } catch (RuntimeException e) {
            if (e instanceof ExpiredJwtException) {
                throw new UnauthorizedException(Error.TOKEN_TIME_EXPIRED_EXCEPTION, Error.TOKEN_TIME_EXPIRED_EXCEPTION.getMessage());
            }
            throw new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage());
//            return false;
        }
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 토큰 내용 확인
    public String getJwtContents(String token) {
        final Claims claims = getBody(token);
        return (String) claims.get("userId");
    }
}
