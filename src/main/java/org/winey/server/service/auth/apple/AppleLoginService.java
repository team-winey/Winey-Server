package org.winey.server.service.auth.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.service.auth.apple.feign.AppleApiClient;
import org.winey.server.service.auth.apple.response.ApplePublicKeys;
import org.winey.server.service.auth.apple.verify.AppleClaimsValidator;
import org.winey.server.service.auth.apple.verify.AppleJwtParser;
import org.winey.server.service.auth.apple.verify.PublicKeyGenerator;

import java.security.PublicKey;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AppleLoginService {

    private final AppleApiClient appleApiClient;
    private final AppleJwtParser appleJwtParser;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public String getAppleId(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleApiClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        return claims.getSubject();
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage());
        }
    }

}
