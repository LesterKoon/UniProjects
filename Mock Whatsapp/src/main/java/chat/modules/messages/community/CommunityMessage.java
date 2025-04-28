package chat.modules.messages.community;

import java.io.Serializable;

public class CommunityMessage implements Serializable {
    private final String communityName;
    private final String sender;
    private final String messageContent;

    public CommunityMessage(String communityName, String sender, String messageContent) {
        this.communityName = communityName;
        this.sender = sender;
        this.messageContent = messageContent;
    }

    public String getCommunityName() {
        return communityName;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
