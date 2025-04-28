package chat.modules.messages.community;

import java.io.Serializable;
import java.util.List;

public class CommunitySession implements Serializable {
    private final String communityName;
    private final String admin; // Admin of the community
    private final List<String> members; // Current members
    private final boolean isCreation; // True for creation, false for joining

    public CommunitySession(String communityName, String admin, List<String> members, boolean isCreation) {
        this.communityName = communityName;
        this.admin = admin;
        this.members = members;
        this.isCreation = isCreation;
    }

    public String getCommunityName() {
        return communityName;
    }

    public String getAdmin() {
        return admin;
    }

    public List<String> getMembers() {
        return members;
    }

    public boolean isCreation() {
        return isCreation;
    }
}
