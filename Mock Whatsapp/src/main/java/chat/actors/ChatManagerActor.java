package chat.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import chat.modules.authentication.LoginResponse;
import chat.modules.messages.personal.ChatSession;
import chat.modules.messages.personal.MessageStatus;
import chat.modules.messages.personal.ReceiveMessage;
import chat.modules.messages.personal.SendMessage;
import chat.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

// This actor is to manage chat sessions between users (mainly for 1-on-1 chats)
public class ChatManagerActor extends AbstractActor {

    private final Map<String, ActorRef> activeChats = new HashMap<>();
    private final Map<String, String> userPaths;

    public ChatManagerActor(Map<String, String> userPaths) {
        this.userPaths = userPaths;
    }

    public static Props props(Map<String, String> userPaths) {
        return Props.create(ChatManagerActor.class, () -> new ChatManagerActor(userPaths));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChatSession.class, this::handleChatSession)
                .match(ReceiveMessage.class, this::handleReceiveMessage)
                .match(SendMessage.class, this::handleSendMessage)
                .build();
    }

    // Handle chat session requests (start/end session)
    private void handleChatSession(ChatSession session) {
        String initiator = session.getInitiator();
        String participant = session.getParticipant();

        System.out.println("Chat session request: " + initiator + " -> " + participant);

        // Check if the user exists in the contacts
        if (!userPaths.containsKey(participant)) {
            getSender().tell(new LoginResponse(false, "User '" + participant + "' does not exist in your contacts."), getSelf());
            return;  
        }

        String initiatorPath = userPaths.get(initiator);
        String participantPath = userPaths.get(participant);

        // Ensure both users are online
        if (initiatorPath == null || participantPath == null) {
            getSender().tell(new LoginResponse(false, "One or both users are not online."), getSelf());
            return;
        }

        // Create actor references
        ActorSelection initiatorActor = getContext().actorSelection(initiatorPath);
        ActorSelection participantActor = getContext().actorSelection(participantPath);

        if (session.isStart()) {
            // Start the chat session and map both users
            activeChats.put(initiator, getSender());
            activeChats.put(participant, getSender());

            initiatorActor.tell(session, getSelf());
            participantActor.tell(session, getSelf());
            getSender().tell(new LoginResponse(true, "=== Starting chat with " + participant), getSelf());
        } else {
            // End the chat session
            activeChats.remove(initiator);
            activeChats.remove(participant);

            initiatorActor.tell(session, getSelf());
            participantActor.tell(session, getSelf());
            getSender().tell(new LoginResponse(true, "Chat session ended."), getSelf());
        }
    }

    // Handle sending messages between users
    private void handleSendMessage(SendMessage msg) {
        String sender = msg.getSender();
        String recipient = msg.getRecipient();
        
        // Ensure both users are online
        if (!userPaths.containsKey(sender) || !userPaths.containsKey(recipient)) {
            getSender().tell(new MessageStatus(msg.getMessageId(), false, 
                "Either sender or recipient is not online."), getSelf());
            return;
        }
    
        String senderPath = userPaths.get(sender);
        String recipientPath = userPaths.get(recipient);
    
        ActorSelection senderActor = getContext().actorSelection(senderPath);
        ActorSelection recipientActor = getContext().actorSelection(recipientPath);
    
        // Check if both are in an active chat session
        if (!activeChats.containsKey(sender) || !activeChats.containsKey(recipient)) {
            senderActor.tell(new MessageStatus(msg.getMessageId(), false, 
                "You are not in an active chat session with this user."), getSelf());
            return;
        }
    
        // Ensure the sender and recipient are in a chat session with each other
        ActorRef senderSession = activeChats.get(sender);
        ActorRef recipientSession = activeChats.get(recipient);
    
        if (senderSession != recipientSession) {
            senderActor.tell(new MessageStatus(msg.getMessageId(), false, 
                "Chat session mismatch. Please start a new session."), getSelf());
            return;
        }
    
        // Validate message content using utility
        if (!ChatUtils.isValidMessage(msg.getMessageContent())) {
            senderActor.tell(new MessageStatus(msg.getMessageId(), false, 
                "Invalid message content."), getSelf());
            return;
        }
    
        // Send the message to the recipient
        recipientActor.tell(new ReceiveMessage(sender, msg.getMessageContent()), getSelf());
        senderActor.tell(new MessageStatus(msg.getMessageId(), true, null), getSelf());
        System.out.println("Message sent from " + sender + " to " + recipient);
    }
    
    // Handle receiving messages from other users
    private void handleReceiveMessage(ReceiveMessage msg) {
        if (activeChats.containsKey(msg.getSender())) {
            activeChats.get(msg.getSender()).tell(msg, getSelf());
        } else {
            System.out.println("[SYSTEM] No active chat session for: " + msg.getSender());
            getSender().tell(new ReceiveMessage("SYSTEM", "No active chat session found."), getSelf());
        }
    }
}


