package chat.modules.messages.personal;
import java.io.Serializable;

public class ChatSession implements Serializable {
    private final String initiator;
    private final String participant;
    private final boolean isStart;

    public ChatSession(String initiator, String participant, boolean isStart) {
        this.initiator = initiator;
        this.participant = participant;
        this.isStart = isStart;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getParticipant() {
        return participant;
    }

    public boolean isStart() {
        return isStart;
    }
}