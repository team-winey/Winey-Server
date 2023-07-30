package org.winey.server.service.auth.apple.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.winey.server.service.auth.apple.response.ApplePublicKeys;

@FeignClient(name = "apple-public-verify-client", url = "https://appleid.apple.com/auth")
public interface AppleApiClient {

    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}