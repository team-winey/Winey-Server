package org.winey.server.domain.recommend;

import lombok.*;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.user.User;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommend extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendId;

    @Column
    @Setter
    private String recommendLink;

    @Column(nullable = false)
    private String recommendTitle;

    @Column
    @Setter
    private String recommendSubTitle;

    @Column
    @Setter
    private Long recommendWon;

    @Column
    @Setter
    private Long recommendPercent;

    @Column(nullable = false)
    private String recommendImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    @Builder
    public Recommend(String recommendTitle, String recommendImage, User user) {
        this.recommendTitle = recommendTitle;
        this.recommendImage = recommendImage;
        this.user = user;
    }
}
