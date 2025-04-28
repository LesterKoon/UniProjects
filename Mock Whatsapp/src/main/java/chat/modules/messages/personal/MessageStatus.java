package chat.modules.messages.personal;

import java.io.Serializable;

public class MessageStatus implements Serializable {
    private final String messageId;
    private final boolean delivered;
    private final String errorMessage;

    public MessageStatus(String messageId, boolean delivered, String errorMessage) {
        this.messageId = messageId;
        this.delivered = delivered;
        this.errorMessage = errorMessage;
    }

    public String getMessageId() {
        return messageId;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}