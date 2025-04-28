package chat.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import chat.modules.status.DeleteStatus;
import chat.modules.status.DeleteStatusResponse;
import chat.modules.status.StatusResponse;
import chat.modules.status.UpdateStatus;
import chat.modules.status.UsersWithStatuses;
import chat.modules.status.ViewStatus;
import chat.modules.messages.online_users.OnlineUsersResponse;
import chat.modules.messages.personal.ReceiveMessage;

import java.util.*;
import java.util.stream.Collectors;

public class StatusUpdateManagerActor extends AbstractActor {
    private final Map<String, List<String>> userStatuses = new HashMap<>();
    private final Map<String, Map<String, List<String>>> statusViewers = new HashMap<>();
    private final Map<String, String> userPaths;  // Injected for notifications

    public StatusUpdateManagerActor(Map<String, String> userPaths) {
        this.userPaths = userPaths;
    }

    public static Props props(Map<String, String> userPaths) {
        return Props.create(StatusUpdateManagerActor.class, () -> new StatusUpdateManagerActor(userPaths));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateStatus.class, this::handleUpdateStatus)
                .match(ViewStatus.class, this::handleViewStatus)
                .match(UsersWithStatuses.class, this::handleGetUsersWithStatuses)
                .match(DeleteStatus.class, this::handleDeleteStatus)
                .build();
    }

    private void handleUpdateStatus(UpdateStatus msg) {
        String username = msg.getUsername();
        String newStatus = msg.getStatus();
    
        // Initialize user's status list if not already present
        userStatuses.putIfAbsent(username, new ArrayList<>());
        userStatuses.get(username).add(newStatus); // Add the new status
    
        System.out.println(username + " updated their status: " + newStatus);
    
        // Notify others about the status update
        broadcastSystemMessage(username + " updated their status!", username);
    }

    private void handleViewStatus(ViewStatus request) {
        String targetUser = request.getUsername();
        String viewer = request.getViewer(); // Ensure viewer is passed correctly
    
        // Get all statuses of the target user
        List<String> statuses = userStatuses.getOrDefault(targetUser, List.of("No statuses available"));
    
        // Notify the viewer of the statuses
        getSender().tell(new StatusResponse(targetUser, statuses), getSelf());
    
        // Track viewers only if the viewer is not the target user
        if (!viewer.equals(targetUser) && !statuses.isEmpty() && userStatuses.containsKey(targetUser)) {
            statusViewers.putIfAbsent(targetUser, new HashMap<>());
            for (String status : statuses) {
                statusViewers.get(targetUser).putIfAbsent(status, new ArrayList<>());
                if (!statusViewers.get(targetUser).get(status).contains(viewer)) {
                    statusViewers.get(targetUser).get(status).add(viewer);
                    notifyCreator(targetUser, status, viewer); // Notify creator
                }
            }
        }
    }

    private void notifyCreator(String targetUser, String status, String viewer) {
        String message = viewer + " viewed your status: " + status;
        if (userPaths.containsKey(targetUser)) {
            getContext().actorSelection(userPaths.get(targetUser))
                .tell(new ReceiveMessage("SYSTEM", message), getSelf());
        }
    }

    private void handleGetUsersWithStatuses(UsersWithStatuses msg) {
        List<String> usersWithStatuses = userStatuses.entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty()) // Only include users with non-empty statuses
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        getSender().tell(new OnlineUsersResponse(usersWithStatuses), getSelf());
    }
    
    private void handleDeleteStatus(DeleteStatus request) {
        String username = request.getUsername();
        String statusToDelete = request.getStatusToDelete();
    
        if (userStatuses.containsKey(username)) {
            List<String> statuses = userStatuses.get(username);
    
            if (statuses.remove(statusToDelete)) {
                if (statuses.isEmpty()) {
                    userStatuses.remove(username); // Remove user if no statuses remain
                }
    
                getSender().tell(new DeleteStatusResponse(true, "Status deleted successfully"), getSelf());
                System.out.println(username + " deleted their status: " + statusToDelete);
    
                // Optional: Remove the status from viewers' records
                if (statusViewers.containsKey(username)) {
                    statusViewers.get(username).remove(statusToDelete);
                }
            } else {
                getSender().tell(new DeleteStatusResponse(false, "Status not found"), getSelf());
            }
        } else {
            getSender().tell(new DeleteStatusResponse(false, "No statuses to delete"), getSelf());
        }
    }

    private void broadcastSystemMessage(String message, String excludeUser) {
        ReceiveMessage broadcastMsg = new ReceiveMessage("SYSTEM", message);
        for (Map.Entry<String, String> entry : userPaths.entrySet()) {
            if (!entry.getKey().equals(excludeUser)) {
                getContext().actorSelection(entry.getValue()).tell(broadcastMsg, getSelf());
            }
        }
    }
}