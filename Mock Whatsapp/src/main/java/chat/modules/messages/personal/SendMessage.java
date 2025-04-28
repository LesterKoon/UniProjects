package chat.modules.messages.personal;

import java.io.Serializable;
import java.util.UUID;

public class SendMessage implements Serializable {
    private final String messageId;
    private final String sender;
    private final String recipient;
    private final String messageContent;

    public SendMessage(String sender, String recipient, String messageContent) {
        this.messageId = UUID.randomUUID().toString();  // Generate unique ID for each message
        this.sender = sender;
        this.recipient = recipient;
        this.messageContent = messageContent;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageContent() {
        return messageContent;
    }
}