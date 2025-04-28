package chat.modules.messages.online_users;

import java.io.Serializable;
import java.util.List;

public class OnlineUsersResponse implements Serializable {
    private final List<String> onlineUsers;

    public OnlineUsersResponse(List<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public List<String> getOnlineUsers() {
        return onlineUsers;
    }
}
