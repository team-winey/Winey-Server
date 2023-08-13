package org.winey.server.domain.notification;

import lombok.*;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.user.User;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends AuditingTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private Long notiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_user")
    private User notiReceiver;

    private String notiMessage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotiType notiType;

    private boolean isChecked; // 유저가 이 알림을 체크했는지

    private Long linkId; // 링크 타고 갈 피드 아이디

    private Long responseId; // 해당 알림을 만드는 반응의 아이디? (commentId or likeId)

    @Builder
    public Notification(User notiReciver, NotiType notiType, String notiMessage, boolean isChecked) {
        this.notiReceiver = notiReciver;
        this.notiType = notiType;
        this.notiMessage = notiMessage;
        this.isChecked = isChecked;
    }

    public void updateLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public void updateResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public void updateIsChecked() {
        this.isChecked = true;
    }


}
