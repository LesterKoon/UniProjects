package chat.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import chat.modules.messages.group.*;
import chat.modules.messages.personal.ReceiveMessage;

import java.util.*;

public class GroupChatManagerActor extends AbstractActor {
    private final Map<String, List<String>> groups = new HashMap<>();
    private final Map<String, String> groupAdmins = new HashMap<>();
    private final Map<String, String> userPaths; // Reference to ServerActor's user paths

    public GroupChatManagerActor(Map<String, String> userPaths) {
        this.userPaths = userPaths;
    }

    public static Props props(Map<String, String> userPaths) {
        return Props.create(GroupChatManagerActor.class, () -> new GroupChatManagerActor(userPaths));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GroupSession.class, this::handleGroupSession)
                .match(GroupMessage.class, this::handleGroupMessage)
                .match(RemoveGroupMember.class, this::handleRemoveGroupMember)
                .match(ExitGroup.class, this::handleExitGroup)
                .build();
    }

    // Handle group session requests (create)
    private void handleGroupSession(GroupSession session) {
        String groupName = session.getGroupName();
        String admin = session.getAdmin();

        if (session.isStart()) {
            if (groups.containsKey(groupName)) {
                getSender().tell(new ReceiveMessage("SYSTEM", "Group already exists"), getSelf());
                return;
            }

            // Include admin in the member list
            List<String> members = new ArrayList<>(session.getMembers());
            if (!members.contains(admin)) {
                members.add(admin);
            }

            groups.put(groupName, members);
            groupAdmins.put(groupName, admin); // Track the admin
            System.out.println("Group created: " + groupName + " by admin: " + admin);

            // Notify creator
            getSender().tell(new ReceiveMessage("SYSTEM", "You have created the group " + groupName), getSelf());

            // Notify other members
            for (String member : members) {
                if (!member.equals(admin) && userPaths.containsKey(member)) {
                    getContext().actorSelection(userPaths.get(member))
                        .tell(new ReceiveMessage("SYSTEM", "You have been added to the group " + groupName + " by " + admin), getSelf());
                }
            }
        } else {
            // 
        }
    }

    private void handleGroupMessage(GroupMessage msg) {
        String groupName = msg.getGroupName();
        String sender = msg.getSender();
    
        // Ensure the group exists before sending messages
        if (!groups.containsKey(groupName)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "Group does not exist."), getSelf());
            return;
        }
    
        // Check if the sender is still a member of the group
        List<String> members = groups.get(groupName);
        if (!members.contains(sender)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "You are no longer part of this group."), getSelf());
            return;
        }
    
        // Send the message to all group members
        for (String member : members) {
            if (userPaths.containsKey(member)) {
                String messageContent = "[Group: " + groupName + "] " + msg.getMessageContent();
                getContext().actorSelection(userPaths.get(member))
                        .tell(new ReceiveMessage(sender, messageContent), getSelf());
            }
        }
        System.out.println(sender + " sent a message in group: " + groupName);
    }

    private void handleRemoveGroupMember(RemoveGroupMember request) {
        String groupName = request.getGroupName();
        String admin = request.getAdmin();
        String member = request.getMember();
    
        if (!groups.containsKey(groupName)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "Group does not exist"), getSelf());
            return;
        }
    
        List<String> members = groups.get(groupName);
    
        // If the admin is leaving themselves
        if (admin.equals(member)) {
            if (members.size() == 1) {
                // Disband the group if admin is the last member
                groups.remove(groupName);
                groupAdmins.remove(groupName);
                getSender().tell(new ReceiveMessage("SYSTEM", "You have left the group. The group has been disbanded."), getSelf());
                return;
            }
    
            // Notify only the other members, not the admin leaving
            members.remove(admin);
            for (String user : members) {
                if (userPaths.containsKey(user)) {
                    getContext().actorSelection(userPaths.get(user))
                        .tell(new ReceiveMessage("SYSTEM", "The admin " + admin + " has left the group."), getSelf());
                }
            }
    
            // Randomly select a new admin
            Random random = new Random();
            String newAdmin = members.get(random.nextInt(members.size()));
            groupAdmins.put(groupName, newAdmin);
    
            // Announce the new admin to all members
            for (String user : members) {
                if (userPaths.containsKey(user)) {
                    getContext().actorSelection(userPaths.get(user))
                        .tell(new ReceiveMessage("SYSTEM", "The new group admin is " + newAdmin), getSelf());
                }
            }
    
            // Notify only the admin leaving
            getSender().tell(new ReceiveMessage("SYSTEM", "You have left the group. The new admin is " + newAdmin), getSelf());
            return;
        }
    
        // Regular member removal (only admin can remove)
        if (!groupAdmins.get(groupName).equals(admin)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "Only the group admin can remove members"), getSelf());
            return;
        }
    
        // Remove the specified member
        if (!members.contains(member)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "The specified user is not in the group"), getSelf());
            return;
        }
    
        members.remove(member);
        getSender().tell(new ReceiveMessage("SYSTEM", member + " has been removed from the group"), getSelf());
    
        if (userPaths.containsKey(member)) {
            getContext().actorSelection(userPaths.get(member))
                .tell(new ReceiveMessage("SYSTEM", "You have been removed from the group " + groupName+ " by " + admin), getSelf());
        }
    }

    private void handleExitGroup(ExitGroup msg) {
        String groupName = msg.getGroupName();
        String exitingUser = msg.getUsername();
    
        if (!groups.containsKey(groupName)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "Group does not exist."), getSelf());
            return;
        }
    
        List<String> members = groups.get(groupName);
        if (!members.contains(exitingUser)) {
            getSender().tell(new ReceiveMessage("SYSTEM", "You are not a member of this group."), getSelf());
            return;
        }
    
        // Remove the user from the group
        members.remove(exitingUser);
    
        // If the user was the admin and members still exist, assign a new admin
        if (groupAdmins.get(groupName).equals(exitingUser)) {
            if (!members.isEmpty()) {
                String newAdmin = members.get(0);
                groupAdmins.put(groupName, newAdmin);
                getSender().tell(new ReceiveMessage("SYSTEM", "You have left the group. The new admin is " + newAdmin), getSelf());
    
                // Notify the remaining members about the new admin
                for (String member : members) {
                    if (userPaths.containsKey(member)) {
                        getContext().actorSelection(userPaths.get(member))
                                .tell(new ReceiveMessage("SYSTEM", "The new admin for group " + groupName + " is " + newAdmin), getSelf());
                    }
                }
            } else {
                // Disband the group if the last member leaves
                groups.remove(groupName);
                groupAdmins.remove(groupName);
                getSender().tell(new ReceiveMessage("SYSTEM", "You have left the group, and it has been disbanded."), getSelf());
            }
        } else {
            getSender().tell(new ReceiveMessage("SYSTEM", "You have successfully left the group."), getSelf());
        }
    }

}
