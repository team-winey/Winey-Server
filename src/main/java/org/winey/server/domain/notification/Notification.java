package org.winey.server.domain.notification;

import lombok.*;
import org.winey.server.domain.user.User;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private Long notiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receive_user")
    private User notiReciver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="request_user")
    private User notiSender;

    private String notiMessage;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotiType notiType;

    private boolean isChecked; //유저가 이 알림을 체크했는지
    private Integer LinkId;

    @Builder
    public Notification(User notiReciver, User notiSender, NotiType notiType, String notiMessage, boolean isChecked){
        this.notiReciver = notiReciver;
        this.notiSender = notiSender;
        this.notiType = notiType;
        this.notiMessage = notiMessage;
        this.isChecked = isChecked;
    }
    public void updateLinkId(Integer linkId) {
        this.LinkId = linkId;
    }


}
