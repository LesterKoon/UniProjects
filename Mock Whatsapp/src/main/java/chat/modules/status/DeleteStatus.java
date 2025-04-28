package chat.modules.status;

import java.io.Serializable;

public class DeleteStatus implements Serializable {
    private final String username;
    private final String statusToDelete;

    public DeleteStatus(String username, String statusToDelete) {
        this.username = username;
        this.statusToDelete = statusToDelete;
    }

    public String getUsername() {
        return username;
    }

    public String getStatusToDelete() {
        return statusToDelete;
    }
}
