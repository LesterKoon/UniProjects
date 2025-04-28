package chat.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import chat.modules.messages.community.CommunityMessage;
import chat.modules.messages.community.CommunitySession;
import chat.modules.messages.community.ExitCommunity;
import chat.modules.messages.community.GetCommunities;
import chat.modules.messages.personal.ReceiveMessage;

import java.util.*;

public class CommunityChatManagerActor extends AbstractActor {
    private final Map<String, List<String>> communities = new HashMap<>();
    private final Map<String, String> communityAdmins = new HashMap<>();
    private final Map<String, String> userPaths; 

    public CommunityChatManagerActor(Map<String, String> userPaths) {
        this.userPaths = userPaths;
    }

    public static Props props(Map<String, String> userPaths) {
        return Props.create(CommunityChatManagerActor.class, () -> new CommunityChatManagerActor(userPaths));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CommunitySession.class, this::handleCommunitySession)
                .match(CommunityMessage.class, this::handleCommunityMessage)
                .match(ExitCommunity.class, this::handleExitCommunity)
                .match(GetCommunities.class, this::handleGetCommunities)
                .match(String.class, msg -> {
                    if (msg.startsWith("CONFIRM_EXIT_COMMUNITY:")) {
                        String[] parts = msg.split(":");
                        String communityName = parts[1];
                        String admin = parts[2];
                        handleAdminCommunityExit(communityName, admin);
                    }
                })
                .build();
    }

    // Handle community session requests (create/join)
    private void handleCommunitySession(CommunitySession session) {
        String communityName = session.getCommunityName();
        String admin = session.getAdmin();
        boolean isCreation = session.isCreation();
    
        if (isCreation) {
            if (communities.containsKey(communityName)) {
                getSender().tell(new ReceiveMessage("SYSTEM", "Community already exists"), getSelf());
                return;
            }
    
            List<String> members = new ArrayList<>();
            members.add(admin); 
    
            communities.put(communityName, members);
            communityAdmins.put(communityName, admin);
    
            getSender().tell(new ReceiveMessage("SYSTEM", "Community '" + communityName + "' created successfully"), getSelf());
            System.out.println("Community created: " + communityName + " by admin: " + admin);
        } else {
            // Prevent joining if the community has been deleted
            if (!communities.containsKey(communityName)) {
                getSender().tell(new ReceiveMessage("SYSTEM", "Community '" + communityName + "' no longer exists."), getSelf());
                return;
            }
    
            List<String> members = communities.get(communityName);
            if (!members.contains(admin)) {
                members.add(admin);
                getSender().tell(new ReceiveMessage("SYSTEM", "You have joined the community '" + communityName + "'"), getSelf());
            } else {
                getSender().tell(new ReceiveMessage("SYSTEM", "You are already a member of '" + communityName + "'"), getSelf());
            }
        }
    }

    // Handle sending messages to communities
    private void handleCommunityMessage(CommunityMessage message) {
        String communityName = message.getCommunityName();
        String sender = message.getSender();

        if (!communities.containsKey(communityName)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "Community does not exist"), getSelf());
            return;
        }

        String admin = communityAdmins.get(communityName);
        if (!admin.equals(sender)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "Only the admin can send messages in this community"), getSelf());
            return;
        }

        List<String> members = communities.get(communityName);
        for (String member : members) {
            if (userPaths.containsKey(member)) {
                getContext().actorSelection(userPaths.get(member))
                    .tell(new ReceiveMessage("SYSTEM", "[Community: " + communityName + "] " + message.getMessageContent()), getSelf());
            }
        }
    }

    // Handle get communities request
    private void handleGetCommunities(GetCommunities request) {
        List<String> communityNames = new ArrayList<>(communities.keySet());
        getSender().tell(new ReceiveMessage("SYSTEM", "Available communities: " + String.join(", ", communityNames)), getSelf());
    }


    // Handle existing communities
    private void handleExitCommunity(ExitCommunity request) {
        String communityName = request.getCommunityName();
        String member = request.getMember();
    
        // Check if community exists
        if (!communities.containsKey(communityName)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "Community '" + communityName + "' does not exist."), getSelf());
            return;
        }
    
        List<String> members = communities.get(communityName);

        // Added Membership Check
        if (!members.contains(member)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "You are not a member of the community."), getSelf());
            return;
        }  
    
        // Admin exiting, delete the entire community
        if (communityAdmins.get(communityName).equals(member)) {
            handleAdminCommunityExit(communityName, member);
            return;
        }
    
        // Remove the member if they are not the admin
        members.remove(member);
        getSender().tell(new ReceiveMessage("SYSTEM", "You have successfully exited the community: " + communityName), getSelf());
    
        // Notify remaining members
        for (String user : members) {
            if (userPaths.containsKey(user)) {
                getContext().actorSelection(userPaths.get(user))
                        .tell(new ReceiveMessage("SYSTEM", member + " has exited the community."), getSelf());
            }
        }
    
        // If no members remain, delete the community
        if (members.isEmpty()) {
            communities.remove(communityName);
            communityAdmins.remove(communityName);
            System.out.println("Community '" + communityName + "' has been disbanded.");
        }
    }

    // Handle admin exiting a community
    private void handleAdminCommunityExit(String communityName, String admin) {
        if (!communities.containsKey(communityName)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "The community no longer exists."), getSelf());
            return;
        }
    
        // Remove the community and the admin
        communities.remove(communityName);
        communityAdmins.remove(communityName);
        
        // Notify the admin
        getSender().tell(new ReceiveMessage("SYSTEM", "You have exited and the community has been deleted."), getSelf());
    
        // Notify all members the community is deleted
        for (String user : userPaths.keySet()) {
            if (userPaths.containsKey(user)) {
                getContext().actorSelection(userPaths.get(user))
                        .tell(new ReceiveMessage("SYSTEM", "The community '" + communityName + "' has been deleted by the admin."), getSelf());
            }
        }
    
        System.out.println("Admin '" + admin + "' has deleted the community: '" + communityName + "'");
    }
}