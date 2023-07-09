package org.winey.server.domain.goal;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Long duringGoalAmount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long duringGoalCount;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isAttained;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    @Builder
    public Goal(Long targetMoney, LocalDate targetDate, User user) {
        this.targetMoney = targetMoney;
        this.targetDate = targetDate;
        this.user = user;
    }
}
