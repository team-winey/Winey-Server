package org.winey.server.domain.goal;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.winey.server.domain.AuditingTimeEntity;
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
    @ColumnDefault("0")
    @Setter
    private Long duringGoalAmount;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Setter
    private boolean isAttained;

    @Column(nullable = false)
    @ColumnDefault("0")
    @Setter
    private Long duringGoalCount;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    @Builder
    public Goal(Long targetMoney, LocalDate targetDate, User user) {
        this.targetMoney = targetMoney;
        this.targetDate = targetDate;
        this.user = user;
    }
    public void updateIsAttained(Goal presentGoal){
        presentGoal.isAttained = true;
    }
    public void updateGoalCountAndAmount(Goal presentGoal, Long feedMoney, boolean createOrDelete){
        if (createOrDelete){
            presentGoal.duringGoalCount+=1;
            presentGoal.duringGoalAmount+=feedMoney;
        }
        else {
            presentGoal.duringGoalCount-=1;
            presentGoal.duringGoalAmount-=feedMoney;
        }
    }
}
