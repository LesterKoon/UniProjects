package chat.actors;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import chat.modules.authentication.LoginResponse;
import chat.modules.authentication.LogoutRequest;
import chat.modules.authentication.RegisterUser;
import chat.modules.messages.community.CommunityMessage;
import chat.modules.messages.community.CommunitySession;
import chat.modules.messages.community.ExitCommunity;
import chat.modules.messages.community.GetCommunities;
import chat.modules.messages.group.ExitGroup;
import chat.modules.messages.group.GroupMessage;
import chat.modules.messages.group.GroupSession;
import chat.modules.messages.group.RemoveGroupMember;
import chat.modules.messages.online_users.GetOnlineUsers;
import chat.modules.messages.online_users.OnlineUsersResponse;
import chat.modules.messages.personal.ChatSession;
import chat.modules.messages.personal.ReceiveMessage;
import chat.modules.messages.personal.SendMessage;
import chat.modules.status.DeleteStatus;
import chat.modules.status.UpdateStatus;
import chat.modules.status.UsersWithStatuses;
import chat.modules.status.ViewStatus;
import chat.utils.ChatUtils;

public class ServerActor extends AbstractActor {
    private final Map<String, String> userPaths = new HashMap<>();  // username -> full actor path

    private final ActorRef chatManagerActor;
    private final ActorRef groupChatManagerActor;
    private final ActorRef communityChatManagerActor;
    private final ActorRef statusUpdateManagerActor;

    public ServerActor() {
        chatManagerActor = getContext().actorOf(ChatManagerActor.props(userPaths), "chatManagerActor");
        groupChatManagerActor = getContext().actorOf(GroupChatManagerActor.props(userPaths), "groupChatManagerActor");
        communityChatManagerActor = getContext().actorOf(CommunityChatManagerActor.props(userPaths), "communityChatManagerActor");
        statusUpdateManagerActor = getContext().actorOf(StatusUpdateManagerActor.props(userPaths), "statusUpdateManagerActor");
    }
    
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RegisterUser.class, this::handleRegisterUser)
                .match(GetOnlineUsers.class, this::handleGetOnlineUsers)
                .match(LogoutRequest.class, this::handleLogoutRequest)

                .match(ChatSession.class, msg -> chatManagerActor.forward(msg, getContext()))
                .match(ReceiveMessage.class, msg -> chatManagerActor.forward(msg, getContext()))
                .match(SendMessage.class, msg -> chatManagerActor.forward(msg, getContext()))

                .match(GroupSession.class, msg -> groupChatManagerActor.forward(msg, getContext()))
                .match(GroupMessage.class, msg -> groupChatManagerActor.forward(msg, getContext()))
                .match(RemoveGroupMember.class, msg -> groupChatManagerActor.forward(msg, getContext()))
                .match(ExitGroup.class, msg -> groupChatManagerActor.forward(msg, getContext()))

                .match(CommunitySession.class, msg -> communityChatManagerActor.forward(msg, getContext()))
                .match(CommunityMessage.class, msg -> communityChatManagerActor.forward(msg, getContext()))
                .match(GetCommunities.class, msg -> communityChatManagerActor.forward(msg, getContext()))
                .match(ExitCommunity.class, msg -> communityChatManagerActor.forward(msg, getContext()))

                .match(UpdateStatus.class, msg -> statusUpdateManagerActor.forward(msg, getContext()))
                .match(UsersWithStatuses.class, msg -> statusUpdateManagerActor.forward(msg, getContext()))
                .match(ViewStatus.class, msg -> statusUpdateManagerActor.forward(msg, getContext()))
                .match(UsersWithStatuses.class, msg -> statusUpdateManagerActor.forward(msg, getContext()))
                .match(DeleteStatus.class, msg -> statusUpdateManagerActor.forward(msg, getContext()))
                
                .build();
    }

    private void broadcastSystemMessage(String message, String excludeUser) {
        ReceiveMessage broadcastMsg = new ReceiveMessage("SYSTEM", message);
        for (Map.Entry<String, String> entry : userPaths.entrySet()) {
            if (!entry.getKey().equals(excludeUser)) {
                getContext().actorSelection(entry.getValue())
                    .tell(broadcastMsg, getSelf());
            }
        }
    }

    private void handleRegisterUser(RegisterUser msg) {
        String username = msg.getUsername();
        ActorRef sender = getSender();
        // Extract the base path and construct the proper user actor path
        String basePath = sender.path().toString().replaceAll("/temp/.*$", "");
        String senderPath = basePath + "/user/userActor";
        
        System.out.println("Registration request from: " + senderPath);
        
        if (!ChatUtils.isValidUsername(username)) {
            sender.tell(new LoginResponse(false, "Invalid username format"), getSelf());
            return;
        }

        if (userPaths.containsKey(username)) {
            sender.tell(new LoginResponse(false, "Username already exists"), getSelf());
        } else {
            // Store the full path
            userPaths.put(username, senderPath);
            System.out.println("User registered: " + username + " at " + senderPath);
            sender.tell(new LoginResponse(true, "Registration successful"), getSelf());

            // Notify other users
            String joinMessage = username + " has joined the server";
            broadcastSystemMessage(joinMessage, username);
        }
    }

    private void handleGetOnlineUsers(GetOnlineUsers msg) {
        OnlineUsersResponse response = new OnlineUsersResponse(userPaths.keySet().stream().toList());
        getSender().tell(response, getSelf());
    }

    private void handleLogoutRequest(LogoutRequest request) {
        String username = request.getUsername();
        if (userPaths.containsKey(username)) {
            // Remove user from online list
            userPaths.remove(username);
            System.out.println("User logged out: " + username);

            // Notify others
            String leaveMessage = username + " has left the server";
            broadcastSystemMessage(leaveMessage, username);
        }
    }

}