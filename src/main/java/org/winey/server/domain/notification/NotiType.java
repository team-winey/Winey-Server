package org.winey.server.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Null;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum NotiType {
    //등급 상승
    RANKUPTO2("기사가 되신 걸 축하해요!", 2),
    RANKUPTO3("귀족이 되신 걸 축하해요!", 3),
    RANKUPTO4("황제가 되신 걸 축하해요!", 4),

    //등급 강등
    RANKDOWNTO3("귀족으로 내려갔어요.", 3),
    RANKDOWNTO2("기사로 내려갔어요.", 2);

    private final String type;
    private Integer level;
}

