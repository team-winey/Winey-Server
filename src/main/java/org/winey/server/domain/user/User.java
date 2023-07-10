package org.winey.server.domain.user;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.recommend.Recommend;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Setter
    @Column(nullable = false)
    @ColumnDefault("1")
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
    public User(String nickname, Long accumulatedAmount, Long feedCount) {
        this.nickname = nickname;
        this.accumulatedAmount = accumulatedAmount;
        this.feedCount = feedCount;
    }

    public void updateUserLevel(UserLevel userLevel){
        this.userLevel = userLevel;
    }
}
