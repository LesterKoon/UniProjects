package chat.modules.status;

import java.io.Serializable;

public class DeleteStatusResponse implements Serializable {
    private final boolean success;
    private final String message;

    public DeleteStatusResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}