package org.winey.server.domain.goal;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.user.User;

@Entity
@Getter
@DynamicInsert
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "goal_type"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @Column(name = "goal_type")
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @Column
    private Long targetMoney;

    @Column
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

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "goal", orphanRemoval = true)
//    private List<Feed> feeds = new ArrayList<>();

    @Builder
    public Goal(GoalType goalType, User user) {
        this.goalType = goalType;
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
