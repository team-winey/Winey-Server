package org.winey.server.service;

import org.springframework.stereotype.Service;
import org.winey.server.service.dto.request.SocialLoginRequest;

public abstract class SocialService {
    public abstract Long login(SocialLoginRequest request);

//    public void logout(Long userId){
//        //어쩌고 저쩌고 로직
//    }
}
