package chat.modules.authentication;

import java.io.Serializable;

public class LogoutRequest implements Serializable {
    private final String username;

    public LogoutRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
