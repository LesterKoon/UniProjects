package chat.modules.messages.community;

import java.io.Serializable;

public class ExitCommunity implements Serializable {
    private final String communityName;
    private final String member;

    public ExitCommunity(String communityName, String member) {
        this.communityName = communityName;
        this.member = member;
    }

    public String getCommunityName() {
        return communityName;
    }

    public String getMember() {
        return member;
    }
}