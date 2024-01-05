package org.winey.server.domain.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedType {
    SAVE("SAVE"),
    CONSUME("CONSUME");

    private final String stringVal;

    public static boolean isValidFeedType(String type) {
        for (FeedType feed : FeedType.values()) {
            if (feed.getStringVal() == type) return true;
        }
        return false;
    }
}
