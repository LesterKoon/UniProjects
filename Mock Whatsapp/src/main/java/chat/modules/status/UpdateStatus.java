package chat.modules.status;

import java.io.Serializable;

public class UpdateStatus implements Serializable {
    private final String username;
    private final String status;

    public UpdateStatus(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}