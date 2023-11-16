package org.winey.server.domain.user;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.comment.Comment;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.feed.FeedLike;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.recommend.Recommend;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = true)
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = true)
    private String fcmToken;

    @Column(nullable = true)
    private Boolean pushNotificationAllowed = true;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Goal> goals;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Recommend> recommends;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Feed> feeds;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private List<FeedLike> feedLikes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "notiReceiver", orphanRemoval = true)
    private List<Notification> notifications;

    @Builder
    public User(String nickname, String socialId, SocialType socialType) {
        this.nickname = nickname;
        this.userLevel = UserLevel.COMMONER;
        this.socialId = socialId;
        this.socialType = socialType;
    }

    public void updateUserLevel(UserLevel userLevel){
        this.userLevel = userLevel;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public void updatePushNotification(Boolean isAllowed){this.pushNotificationAllowed = isAllowed;}
}
