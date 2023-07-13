package org.winey.server.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.SocialPlatform;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialPlatform socialPlatform;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    private SocialUser(String nickname, String profileImage, SocialPlatform socialPlatform, String accessToken, String refreshToken) {
        this.nickName = nickname;
        this.profileImage = profileImage;
        this.socialPlatform = socialPlatform;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static SocialUser of(String nickName, String profileImage, SocialPlatform socialPlatform, String accessToken, String refreshToken) {
        return new SocialUser(nickName, profileImage, socialPlatform, accessToken, refreshToken);
    }



}
