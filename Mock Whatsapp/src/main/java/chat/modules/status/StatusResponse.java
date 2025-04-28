package chat.modules.status;

import java.io.Serializable;
import java.util.List;

public class StatusResponse implements Serializable {
    private final String username;
    private final List<String> statuses;

    public StatusResponse(String username, List<String> statuses) {
        this.username = username;
        this.statuses = statuses;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getStatuses() {
        return statuses;
    }
}