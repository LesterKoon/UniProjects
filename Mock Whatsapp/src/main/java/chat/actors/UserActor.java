package chat.actors;

import akka.actor.AbstractActor;
import chat.main.UserMain;
import chat.modules.authentication.LoginResponse;
import chat.modules.messages.online_users.OnlineUsersResponse;
import chat.modules.messages.personal.ChatSession;
import chat.modules.messages.personal.MessageStatus;
import chat.modules.messages.personal.ReceiveMessage;
import chat.modules.status.DeleteStatusResponse;
import chat.modules.status.StatusResponse;
import chat.modules.status.StatusUpdateNotification;

public class UserActor extends AbstractActor {
    private final String username;
    private boolean isFirstMessageInChat = true;
    public UserActor(String username) {
        this.username = username;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReceiveMessage.class, this::handleReceiveMessage)
                .match(LoginResponse.class, this::handleLoginResponse)
                .match(OnlineUsersResponse.class, this::handleOnlineUsers)
                .match(ChatSession.class, this::handleChatSession)
                .match(MessageStatus.class, this::handleMessageStatus)
                .match(StatusUpdateNotification.class, this::handleStatusUpdateNotification)
                .match(StatusResponse.class, this::handleStatusResponse)
                .match(DeleteStatusResponse.class, this::handleDeleteStatusResponse)
                .matchAny(msg -> System.out.println("\nUnhandled message: " + msg + "\n"))
                .build();
    }

    private void handleReceiveMessage(ReceiveMessage msg) {
        // Avoid printing messages sent by this user (self-messages)
        if (msg.getSender().equals(username)) {
            return; // Do not print self-messages
        }

        if (msg.getSender().equals("SYSTEM")) {
            if (!msg.getMessageContent().contains("session")) {
                System.out.println("\n\n[SYSTEM] " + msg.getMessageContent());
            }
        } else {
            if (isFirstMessageInChat) {
                System.out.println();
                isFirstMessageInChat = false;
            }
            System.out.println(msg.getSender() + ": " + msg.getMessageContent());
        }
    }


    private void handleLoginResponse(LoginResponse response) {
        if (!UserMain.isInChatMode()) {
            System.out.println("\n" + response.getMessage() + "\n");
        }
    }

    private void handleMessageStatus(MessageStatus status) {
        if (UserMain.isInChatMode()) {
            if (status.isDelivered()) {
                System.out.println("[Message sent]");
            } else {
                System.out.println("[Message failed: " + status.getErrorMessage() + "]");
            }
        }
    }

    private void handleOnlineUsers(OnlineUsersResponse response) {
        System.out.println("\nOnline users: " + String.join(", ", response.getOnlineUsers()) + "\n");
    }

    private void handleChatSession(ChatSession session) {
        if (session.isStart()) {
            String partner = session.getInitiator().equals(username) ? 
                        session.getParticipant() : session.getInitiator();

            // Check if the user is already in chat mode with this partner
            if (!UserMain.isInChatMode() || !partner.equals(UserMain.getCurrentChatPartner())) {
                System.out.println("\n\n=== Starting chat with " + partner + " ===");
                System.out.println("Type 'EXIT' to end the chat\n");
                isFirstMessageInChat = true;
                UserMain.enterChatMode(partner);
            }
        } else {
            System.out.println("\n=== Chat session ended ===\n");
            UserMain.exitChatMode();
        }
    }

    private void handleStatusUpdateNotification(StatusUpdateNotification notification) {
        System.out.println("\n[SYSTEM] " + notification.getUsername() + " updated their status.\n");
    }

    private void handleStatusResponse(StatusResponse response) {
        System.out.println("\n" + response.getUsername() + "'s Statuses:");
        for (String status : response.getStatuses()) {
            System.out.println("- " + status);
        }
        System.out.println();
    }

    private void handleDeleteStatusResponse(DeleteStatusResponse response) {
        System.out.println("\n[SYSTEM] " + response.getMessage() + "\n");
    }
}