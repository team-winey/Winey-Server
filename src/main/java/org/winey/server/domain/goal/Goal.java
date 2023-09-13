package org.winey.server.domain.goal;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @Column(nullable = false)
    private Long targetMoney;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false)
    private Long duringGoalAmount;

    @Column(nullable = false)
    private boolean isAttained;

    @Column(nullable = false)
    private Long duringGoalCount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "goal", orphanRemoval = true)
    private List<Feed> feeds = new ArrayList<>();

    @Builder
    public Goal(Long targetMoney, LocalDate targetDate, User user) {
        this.targetMoney = targetMoney;
        this.targetDate = targetDate;
        this.user = user;
        this.duringGoalCount = 0L;
        this.isAttained = false;
        this.duringGoalAmount = 0L;

    }

    public void updateIsAttained(boolean isAttained) {
        this.isAttained = isAttained;
    }

    public void updateGoalCountAndAmount(Long feedMoney, boolean createOrDelete) {
        if (createOrDelete) {
            this.duringGoalCount += 1;
            this.duringGoalAmount += feedMoney;
        } else {
            this.duringGoalCount -= 1;
            this.duringGoalAmount -= feedMoney;
        }
    }

    public void resetGoalCountAndAmount(){
            this.duringGoalAmount = 0L;
            this.duringGoalCount = 0L;
    }
}
