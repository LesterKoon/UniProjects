package chat.modules.messages.group;

import java.io.Serializable;

public class RemoveGroupMember implements Serializable {
    private final String groupName;
    private final String admin;
    private final String member;

    public RemoveGroupMember(String groupName, String admin, String member) {
        this.groupName = groupName;
        this.admin = admin;
        this.member = member;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getAdmin() {
        return admin;
    }

    public String getMember() {
        return member;
    }
}
