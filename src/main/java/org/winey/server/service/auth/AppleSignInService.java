package org.winey.server.service.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleSignInService {

    public String getAppleData(String accessToken){
        return "";
    }
}
