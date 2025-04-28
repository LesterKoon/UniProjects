package chat.modules.authentication;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private final boolean success;
    private final String message;

    public LoginResponse(boolean success, String message) {
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
