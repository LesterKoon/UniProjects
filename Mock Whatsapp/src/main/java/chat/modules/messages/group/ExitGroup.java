package chat.modules.messages.group;

import java.io.Serializable;

public class ExitGroup implements Serializable {
    private final String groupName;
    private final String username;

    public ExitGroup(String groupName, String username) {
        this.groupName = groupName;
        this.username = username;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getUsername() {
        return username;
    }
}