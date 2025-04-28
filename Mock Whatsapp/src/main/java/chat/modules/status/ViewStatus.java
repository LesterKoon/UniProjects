package chat.modules.status;

import java.io.Serializable;

public class ViewStatus implements Serializable {
    private final String username; 
    private final String viewer;

    public ViewStatus(String username, String viewer) {
        this.username = username;
        this.viewer = viewer;
    }

    public String getUsername() {
        return username;
    }

    public String getViewer() {
        return viewer;
    }
}