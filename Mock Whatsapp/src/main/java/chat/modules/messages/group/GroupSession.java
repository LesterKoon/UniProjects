package chat.modules.messages.group;

import java.io.Serializable;
import java.util.List;

public class GroupSession implements Serializable {
    private final String groupName;
    private final String admin; // The group admin
    private final List<String> members;
    private final boolean start; // true for creation, false for disbanding

    public GroupSession(String groupName, String admin, List<String> members, boolean start) {
        this.groupName = groupName;
        this.admin = admin;
        this.members = members;
        this.start = start;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getAdmin() {
        return admin;
    }

    public List<String> getMembers() {
        return members;
    }

    public boolean isStart() {
        return start;
    }
}
