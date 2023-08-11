package org.winey.server.domain.notification;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @JoinColumn(name="user_id")
    private User notiUser;

    private String notiMessage;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotiType notiType;

    private boolean isChecked; //유저가 이 알림을 체크했는지

    @Builder
    public Notification(User user, NotiType notiType, String notiMessage, boolean isChecked){
        this.notiUser = user;
        this.notiType = notiType;
        this.notiMessage = notiMessage;
        this.isChecked = isChecked;
    }




}
