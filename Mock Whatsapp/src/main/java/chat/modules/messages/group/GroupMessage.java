package chat.modules.messages.group;

import java.io.Serializable;

public class GroupMessage implements Serializable {
    private final String groupName;
    private final String sender;
    private final String messageContent;

    public GroupMessage(String groupName, String sender, String messageContent) {
        this.groupName = groupName;
        this.sender = sender;
        this.messageContent = messageContent;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
