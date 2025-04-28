package chat.modules.status;

import java.io.Serializable;

public class StatusUpdateNotification implements Serializable {
    private final String username;

    public StatusUpdateNotification(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}