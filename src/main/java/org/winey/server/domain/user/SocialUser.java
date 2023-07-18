package org.winey.server.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.recommend.Recommend;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"email", "socialType"}
        )
})
public class SocialUser extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

    @Column(nullable = false)
    private Long accumulatedAmount;

    @Column(nullable = false)
    private Long feedCount;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String refreshToken;

    @Column(nullable = false)
    private SocialType socialType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Goal> goals;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Recommend> recommends;

    @Builder
    public SocialUser(String nickname, String email, SocialType socialType) {
        this.nickname = nickname;
        this.userLevel = UserLevel.COMMONER;
        this.accumulatedAmount = 0L;
        this.feedCount = 0L;
        this.email = email;
        this.socialType = socialType;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
