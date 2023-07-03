package org.winey.server.domain.recommend;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
    private String recommendLink;

    @Column(nullable = false)
    private String recommendTitle;

    @Column(nullable = false)
    private Long recommendPrice;

    @Column(nullable = false)
    private String recommendImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    @Builder
    public Recommend(String recommendLink, String recommendTitle, Long recommendPrice, String recommendImage, User user) {
        this.recommendLink = recommendLink;
        this.recommendTitle = recommendTitle;
        this.recommendPrice = recommendPrice;
        this.recommendImage = recommendImage;
        this.user = user;
    }
}
