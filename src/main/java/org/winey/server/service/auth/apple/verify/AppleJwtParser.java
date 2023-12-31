package org.winey.server.service.auth.apple.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.CustomException;

import java.security.PublicKey;
import java.util.Map;

@Component
public class AppleJwtParser {

    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Map<String, String> parseHeaders(String identityToken) {
        try {
            String encodedHeader = identityToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            String decodedHeader = new String(Base64Utils.decodeFromUrlSafeString(encodedHeader));
            return OBJECT_MAPPER.readValue(decodedHeader, Map.class);

        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new CustomException(Error.INVALID_APPLE_IDENTITY_TOKEN, Error.INVALID_APPLE_IDENTITY_TOKEN.getMessage());
        }
    }

    public Claims parsePublicKeyAndGetClaims(String idToken, PublicKey publicKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new CustomException(Error.EXPIRED_APPLE_IDENTITY_TOKEN, Error.EXPIRED_APPLE_IDENTITY_TOKEN.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new CustomException(Error.INVALID_APPLE_IDENTITY_TOKEN, Error.INVALID_APPLE_IDENTITY_TOKEN.getMessage());
        }
    }
}