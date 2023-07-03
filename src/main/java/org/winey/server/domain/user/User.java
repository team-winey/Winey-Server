package org.winey.server.domain.user;

import lombok.*;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.recommend.Recommend;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private UserLevel userLevel;

    @Column(nullable = false)
    private Long accumulatedAmount;

    @Column(nullable = false)
    private Long feedCount;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Goal> goals;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Recommend> recommends;

    @Builder
    public User(String nickname, UserLevel userLevel, Long accumulatedAmount, Long feedCount) {
        this.nickname = nickname;
        this.userLevel = userLevel;
        this.accumulatedAmount = accumulatedAmount;
        this.feedCount = feedCount;
    }
}
