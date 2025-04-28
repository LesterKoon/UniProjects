package chat.modules.messages.personal;

import java.io.Serializable;

public class ReceiveMessage implements Serializable {
    private final String sender;
    private final String messageContent;

    public ReceiveMessage(String sender, String messageContent) {
        this.sender = sender;
        this.messageContent = messageContent;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
