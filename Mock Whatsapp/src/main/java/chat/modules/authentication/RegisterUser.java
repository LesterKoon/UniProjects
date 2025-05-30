package chat.modules.authentication;

import java.io.Serializable;

public class RegisterUser implements Serializable {
    private final String username;
    private final String password;

    public RegisterUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
