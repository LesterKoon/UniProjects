package chat.main;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import chat.actors.UserActor;
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
import chat.modules.status.DeleteStatusResponse;
import chat.modules.status.StatusResponse;
import chat.modules.status.UpdateStatus;
import chat.modules.status.UsersWithStatuses;
import chat.modules.status.ViewStatus;
import chat.utils.ChatUtils;
import scala.compat.java8.FutureConverters;

public class UserMain {
    private static ActorSelection serverActor;
    private static ActorRef userActor;
    private static String username;
    private static volatile String currentChatPartner = null;
    private static volatile boolean inChatMode = false;


    public static void main(String[] args) {
        Config regularConfig = ConfigFactory.load();
        Config clientConfig = regularConfig.getConfig("client")
                .withFallback(regularConfig);
        ActorSystem system = ActorSystem.create("ChatClientSystem", clientConfig);
    
        Scanner scanner = new Scanner(System.in);
    
        serverActor = system.actorSelection("akka://ChatServerSystem@127.0.0.1:2552/user/serverActor");
        
        // If unable to connect to the server
        if (serverActor == null) {
            System.out.println("\n❌ Error: Unable to connect to the server. Please check the server address.");
            system.terminate();
            return;
        }
        // Colours for UI
        String RESET = "\033[0m";
        String GREEN = "\033[32m";
        System.out.println("===========================================================");
        System.out.println(GREEN+"");
        System.out.println("                        ▄▄█▀▀▀▀▀█▄▄                        ");
        System.out.println("                      ▄█▀  ▄▄     ▀█▄                      ");
        System.out.println("                      █   ███       █                      ");
        System.out.println("                      █   ██▄       █                      ");
        System.out.println("                      █    ▀██▄ ██  █                      ");
        System.out.println("                      █      ▀███▀  █                      ");
        System.out.println("                      ▀█▄         ▄█▀                      ");
        System.out.println("                        ▄█   ▄▄▄▄█▀▀                       ");
        System.out.println("                        ▀▀▀▀                               ");
        System.out.println("");
        System.out.println(RESET+"===========================================================");

        System.out.println("                  Welcome to the chat app!");
        System.out.println("-----------------------------------------------------------");

        System.out.print(RESET+"                          LOGIN\n");
        boolean validUsername = false;
        
        while (!validUsername) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();
            
            if (!ChatUtils.isValidUsername(username)) {
                System.out.println("\nInvalid username format. A username must be 3-15 alphanumeric characters. Please try again.\n");
                continue;
            }
    
            // Register and ensure the serverActor is correctly initialized
            if (registerWithServer(username, password)) {
                validUsername = true;
            } else {
                System.out.println("\n❌ Registration failed. Please try a different username.\n");
            }
        }
    
        // Create user actor only after successful registration
        userActor = system.actorOf(Props.create(UserActor.class, username), "userActor");
        System.out.println("✅ Connected to the server successfully!\n");
    
        boolean running = true;
        while (running) {
            try {
                if (inChatMode) {
                    running = handleChatInput(scanner);
                } else {
                    running = handleMainMenu(scanner);
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage() + "\n");
            }
        }
    
        notifyServerLogout();
        system.terminate();
    }

    private static boolean registerWithServer(String username, String password) {
        // Ensure serverActor is initialized before proceeding
        if (serverActor == null) {
            System.out.println("\n❌ Server connection error. Please try again later.\n");
            return false;
        }
    
        Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
        try {
            CompletableFuture<Object> registrationFuture = FutureConverters
                    .toJava(Patterns.ask(serverActor, new RegisterUser(username, password), timeout))
                    .toCompletableFuture();
    
            Object response = registrationFuture.get();
            if (response instanceof LoginResponse) {
                LoginResponse loginResponse = (LoginResponse) response;
                if (!loginResponse.isSuccess()) {
                    System.out.println("\n❌ Registration failed: " + loginResponse.getMessage());
                    return false;
                }
                System.out.println("\n✅ Successfully registered as: " + username);
                return true;
            }
        } catch (Exception e) {
            System.out.println("\n❌ Could not connect to the server. Please try again later.");
            e.printStackTrace();
        }
        return false;
    }

    private static void notifyServerLogout() {
        if (serverActor != null && userActor != null) {
            serverActor.tell(new LogoutRequest(username), userActor);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    ///                                MAIN MENU LOGIC                                    ///
    /////////////////////////////////////////////////////////////////////////////////////////
    private static boolean handleMainMenu(Scanner scanner) {
        System.out.println("===========================================================");
        System.out.println("                        Main Menu:");
        System.out.println("===========================================================");
        System.out.println("1. View online users");
        System.out.println("2. Start chat session");
        System.out.println("3. Start group chat session");
        System.out.println("4. Start community session");
        System.out.println("5. Status update");
        System.out.println("6. Exit");
        System.out.println("===========================================================");
        System.out.print("\nChoose an option [Enter number]: ");
    
        String input = scanner.nextLine();
        if (input.isEmpty()) return true;
    
        try {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1 -> handleViewOnlineUsers();
                case 2 -> startChatSession(scanner);
                case 3 -> handleGroupChatMenu(scanner); // Group Chat Sub-menu
                case 4 -> handleCommunitySessionMenu(scanner); // Community Session Sub-menu
                case 5 -> handleStatusUpdateMenu(scanner); // Status Update Sub-menu
                case 6 -> {
                    return false;
                }
                default -> System.out.println("\nInvalid option. Please try again.\n");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number.2\n");
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage() + "\n");
        }
        return true;
    }

    // Sub main menu for group chat
    private static void handleGroupChatMenu(Scanner scanner) {
        boolean inGroupChatMenu = true;
        while (inGroupChatMenu) {
            System.out.println("\n===========================================================");
            System.out.println("                   Group Chat Session Menu:");
            System.out.println("===========================================================");
            System.out.println("1. Create a group");
            System.out.println("2. Send a group message");
            System.out.println("3. Remove a member from the group");
            System.out.println("4. Exit group");
            System.out.println("5. Back to main menu");
            System.out.println("===========================================================");
            System.out.print("\nChoose an option [Enter number]: ");
    
            String input = scanner.nextLine();
            if (input.isEmpty()) continue;
    
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> createGroup(scanner);
                    case 2 -> sendGroupMessage(scanner);
                    case 3 -> removeGroupMember(scanner);
                    case 4 -> exitGroup(scanner);
                    case 5 -> inGroupChatMenu = false;
                    default -> System.out.println("\nInvalid option. Please try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number.\n");
            }
        }
    }

    // Sub main menu for community session
    private static void handleCommunitySessionMenu(Scanner scanner) {
        boolean inCommunitySessionMenu = true;
        while (inCommunitySessionMenu) {
            System.out.println("\n===========================================================");
            System.out.println("                   Community Session Menu:");
            System.out.println("===========================================================");
            System.out.println("1. Create a new community");
            System.out.println("2. Join a community");
            System.out.println("3. Message community");
            System.out.println("4. Exit community");
            System.out.println("5. Back to main menu");
            System.out.println("===========================================================");
            System.out.print("\nChoose an option [Enter number]: ");
    
            String input = scanner.nextLine();
            if (input.isEmpty()) continue;
    
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> createCommunity(scanner);
                    case 2 -> joinCommunity(scanner);
                    case 3 -> messageCommunity(scanner);
                    case 4 -> exitCommunity(scanner);  // Added here
                    case 5 -> inCommunitySessionMenu = false;
                    default -> System.out.println("\nInvalid option. Please try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number.\n");
            }
        }
    }

    // Sub main menu for status update
    private static void handleStatusUpdateMenu(Scanner scanner) {
        boolean inStatusUpdateMenu = true;
        while (inStatusUpdateMenu) {
            System.out.println("\n===========================================================");
            System.out.println("                     Status Update Menu:");
            System.out.println("===========================================================");
            System.out.println("1. Write a status");
            System.out.println("2. View statuses");
            System.out.println("3. Delete status");
            System.out.println("4. Back to main menu");
            System.out.println("===========================================================");
            System.out.print("\nChoose an option [Enter number]: ");
            
            String input = scanner.nextLine();
            if (input.isEmpty()) continue;
    
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> writeStatus(scanner);
                    case 2 -> viewStatus(scanner);
                    case 3 -> deleteStatus(scanner);
                    case 4 -> inStatusUpdateMenu = false; // Exit sub-menu
                    default -> System.out.println("\nInvalid option. Please try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number.\n");
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ///                               VIEW ONLINE USERS                                   ///
    /////////////////////////////////////////////////////////////////////////////////////////
    private static void handleViewOnlineUsers() throws Exception {
        Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
        CompletableFuture<Object> usersFuture = FutureConverters
            .toJava(Patterns.ask(serverActor, new GetOnlineUsers(), timeout))
            .toCompletableFuture();
        
        OnlineUsersResponse response = (OnlineUsersResponse) usersFuture.get();
        //System.out.println("\nOnline users: " + String.join(", ", response.getOnlineUsers()) + "\n");
        List<String> onlineUsers = response.getOnlineUsers();
        System.out.println("\nOnline Users:");
        for (String user: onlineUsers){
            System.out.println("- " + user);
        }
        System.out.println();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ///                                  HANDLE CHAT INPUT                                ///
    /////////////////////////////////////////////////////////////////////////////////////////
    private static boolean handleChatInput(Scanner scanner) {
        String message = scanner.nextLine().trim();
        
        if (message.equalsIgnoreCase("EXIT")) {
            ChatSession endSession = new ChatSession(username, currentChatPartner, false);
            serverActor.tell(endSession, userActor);
            return true;
        }
        
        if (!message.isEmpty()) {
            sendChatMessage(message);
        }
        return true;
    }

    private static void startChatSession(Scanner scanner) {
        boolean validUserSelected = false;
    
        while (!validUserSelected) {
            System.out.print("\nEnter username to chat with: ");
            String participant = scanner.nextLine();
    
            serverActor.tell(new ChatSession(username, participant, true), userActor);
            if (participant.trim().isEmpty()) {
                System.out.println("\n[SYSTEM] Please enter a valid username.\n");
                continue;
            }
    
            Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
            CompletableFuture<Object> chatFuture = FutureConverters
                .toJava(Patterns.ask(serverActor, new ChatSession(username, participant, true), timeout))
                .toCompletableFuture();
    
            try {
                Object response = chatFuture.get(); 
                if (response instanceof LoginResponse) {
                    LoginResponse loginResponse = (LoginResponse) response;
                    if (loginResponse.isSuccess()) {
                        System.out.println("\n[SYSTEM] Chat session started with " + participant + ".\n");
                        enterChatMode(participant); 
                        validUserSelected = true;  
                    } else {
                        System.out.println("\n[SYSTEM] " + loginResponse.getMessage());
                        System.out.println("Please enter a valid username again.\n");
                    }
                }
            } catch (Exception e) {
                System.out.println("\n[SYSTEM] Error: " + e.getMessage() + "\n");
            }
        }
    }

    // Send the chat message
    private static void sendChatMessage(String message) {
        if (currentChatPartner != null) {
            SendMessage chatMsg = new SendMessage(username, currentChatPartner, message);
            serverActor.tell(chatMsg, userActor);
        }
    }

    public static boolean isInChatMode() {
        return inChatMode;
    }

    public static void enterChatMode(String partner) {
        currentChatPartner = partner;
        inChatMode = true;
    }

    public static void exitChatMode() {
        currentChatPartner = null;
        inChatMode = false;
        System.out.println("\nReturned to main menu");
    }

    public static String getCurrentChatPartner() {
        return currentChatPartner;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    ///                                 GROUP CHAT SESSION                                ///
    /////////////////////////////////////////////////////////////////////////////////////////
    // private static void createGroup(Scanner scanner) {
    //     System.out.print("\nEnter group name: ");
    //     String groupName = scanner.nextLine();
    
    //     System.out.print("Enter usernames to add (comma-separated): ");
    //     String[] members = scanner.nextLine().split(",");
    
    //     GroupSession groupSession = new GroupSession(groupName, username, List.of(members), true);
    //     serverActor.tell(groupSession, userActor);
    // }    

    private static void createGroup(Scanner scanner) {
    System.out.print("\nEnter group name: ");
    String groupName = scanner.nextLine().trim();
    
    // Ensure the group name is not empty
    if (groupName.isEmpty()) {
        System.out.println("\n[SYSTEM] Group name cannot be empty. Please try again.");
        return;
    }

    boolean validInput = false;
    String[] members = new String[0];
    
    while (!validInput) {
        System.out.print("Enter usernames to add (comma-separated, at least 2 members): ");
        String input = scanner.nextLine().trim();
        
        // Split the input and remove extra spaces
        members = Arrays.stream(input.split(","))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .toArray(String[]::new);
        
        if (members.length >= 2) {
            validInput = true;
        } else {
            System.out.println("\n[SYSTEM] You must enter at least 2 usernames to create a group. Please try again.\n");
        }
    }

    // ✅ Create the group session with validated inputs
    GroupSession groupSession = new GroupSession(groupName, username, List.of(members), true);
    serverActor.tell(groupSession, userActor);
    System.out.println("\n[SYSTEM] Group '" + groupName + "' created successfully!");
}
    private static void sendGroupMessage(Scanner scanner) {
        System.out.print("\nEnter group name: ");
        String groupName = scanner.nextLine();

        System.out.println("Enter your messages (type BACK to exit):");
        while (true) {
            String message = scanner.nextLine().trim();
            if (message.equalsIgnoreCase("BACK")) break;

            GroupMessage groupMessage = new GroupMessage(groupName, username, message);
            serverActor.tell(groupMessage, userActor);
        }
    }

    private static void removeGroupMember(Scanner scanner) {
        System.out.print("\nEnter group name: ");
        String groupName = scanner.nextLine();

        System.out.print("Enter username to remove: ");
        String memberToRemove = scanner.nextLine();

        if (groupName.trim().isEmpty() || memberToRemove.trim().isEmpty()) {
            System.out.println("\n[SYSTEM] Group name or username cannot be empty.\n");
            return; // Prevent menu display if input is invalid
        }

        // Send the request and wait for the response before continuing
        Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
        CompletableFuture<Object> removeFuture = FutureConverters
            .toJava(Patterns.ask(serverActor, new RemoveGroupMember(groupName, username, memberToRemove), timeout))
            .toCompletableFuture();

        try {
            Object response = removeFuture.get(); // Block and wait for server's response
            if (response instanceof ReceiveMessage) {
                ReceiveMessage msg = (ReceiveMessage) response;
                System.out.println("\n[SYSTEM] " + msg.getMessageContent());  // Show the message before the menu
            }
        } catch (Exception e) {
            System.out.println("\n[SYSTEM] Error: " + e.getMessage() + "\n");
        }
    }

    private static void exitGroup(Scanner scanner) {
        System.out.print("\nEnter the group name you want to exit: ");
        String groupName = scanner.nextLine().trim();
    
        if (groupName.isEmpty()) {
            System.out.println("\n[SYSTEM] Group name cannot be empty.\n");
            return;
        }
    
        Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
        CompletableFuture<Object> exitFuture = FutureConverters
                .toJava(Patterns.ask(serverActor, new ExitGroup(groupName, username), timeout))
                .toCompletableFuture();
    
        try {
            Object response = exitFuture.get();
            if (response instanceof ReceiveMessage) {
                ReceiveMessage message = (ReceiveMessage) response;
                System.out.println("\n[SYSTEM] " + message.getMessageContent());
            }
        } catch (Exception e) {
            System.out.println("\n[SYSTEM] Error: " + e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ///                                 COMMUNITY SESSION                                 ///
    /////////////////////////////////////////////////////////////////////////////////////////
    private static void createCommunity(Scanner scanner) {
        System.out.print("\nEnter community name: ");
        String communityName = scanner.nextLine();

        CommunitySession session = new CommunitySession(communityName, username, List.of(), true);
        serverActor.tell(session, userActor);
    }

    private static void joinCommunity(Scanner scanner) {
        System.out.println("\nFetching list of available communities...");
        serverActor.tell(new GetCommunities(), userActor);

        System.out.print("\nEnter the name of the community to join: ");
        String communityName = scanner.nextLine();

        CommunitySession session = new CommunitySession(communityName, username, List.of(), false);
        serverActor.tell(session, userActor);
    }

    private static void messageCommunity(Scanner scanner) {
        System.out.print("\nEnter community name: ");
        String communityName = scanner.nextLine();

        System.out.println("Enter your messages (type BACK to exit):");
        while (true) {
            String message = scanner.nextLine().trim();
            if (message.equalsIgnoreCase("BACK")) break;

            CommunityMessage communityMessage = new CommunityMessage(communityName, username, message);
            serverActor.tell(communityMessage, userActor);
        }
    }

    private static void exitCommunity(Scanner scanner) {
        System.out.print("\nEnter the community name you want to exit: ");
        String communityName = scanner.nextLine();

        if (communityName.trim().isEmpty()) {
            System.out.println("\n[SYSTEM] Community name cannot be empty.\n");
            return;
        }

        // Send the exit request to the server
        Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
        CompletableFuture<Object> exitFuture = FutureConverters
            .toJava(Patterns.ask(serverActor, new ExitCommunity(communityName, username), timeout))
            .toCompletableFuture();

        try {
            Object response = exitFuture.get();
            if (response instanceof ReceiveMessage) {
                ReceiveMessage message = (ReceiveMessage) response;

                // If the admin receives a confirmation request
                if (message.getMessageContent().contains("Confirm exit?")) {
                    System.out.print("\n[SYSTEM] " + message.getMessageContent() + " ");
                    String confirmation = scanner.nextLine();
                    if (confirmation.equalsIgnoreCase("yes")) {
                        serverActor.tell("CONFIRM_EXIT_COMMUNITY:" + communityName + ":" + username, userActor);
                    } else {
                        System.out.println("\n[SYSTEM] Exit cancelled.\n");
                    }
                } else {
                    System.out.println("\n[SYSTEM] " + message.getMessageContent() + "\n");
                }
            }
        } catch (Exception e) {
            System.out.println("\n[SYSTEM] Error: " + e.getMessage() + "\n");
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    ///                                     STATUS                                        ///
    /////////////////////////////////////////////////////////////////////////////////////////
    private static void writeStatus(Scanner scanner) {
        System.out.print("\nEnter your status: ");
        String status = scanner.nextLine();
        serverActor.tell(new UpdateStatus(username, status), userActor);
        System.out.println("\nStatus updated!\n");
    }

    private static void viewStatus(Scanner scanner) {
        System.out.println("\nFetching users with statuses...");
    
        Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
        CompletableFuture<Object> usersFuture = FutureConverters
                .toJava(Patterns.ask(serverActor, new UsersWithStatuses(), timeout))
                .toCompletableFuture();
    
        try {
            Object response = usersFuture.get(); // Wait for response from the server
            if (response instanceof OnlineUsersResponse) {
                OnlineUsersResponse usersResponse = (OnlineUsersResponse) response;
                List<String> usersWithStatuses = usersResponse.getOnlineUsers();
                
                if (usersWithStatuses.isEmpty()) {
                    System.out.println("\nNo users have updated their statuses.\n");
                    return;
                }
    
                System.out.println("\nUsers with statuses: " + String.join(", ", usersWithStatuses));
                System.out.print("\nEnter username to view status: ");
                String targetUser = scanner.nextLine();
    
                // Using `ask` instead of `tell` for synchronous status retrieval
                CompletableFuture<Object> statusFuture = FutureConverters
                    .toJava(Patterns.ask(serverActor, new ViewStatus(targetUser, username), timeout))
                    .toCompletableFuture();
    
                // Wait for the response before showing the menu
                Object statusResponse = statusFuture.get();
                if (statusResponse instanceof StatusResponse) {
                    StatusResponse statusResp = (StatusResponse) statusResponse;
                    if (statusResp.getStatuses().isEmpty()) {
                        System.out.println("\nNo statuses available for " + targetUser);
                    } else {
                        System.out.println("\n" + targetUser + "'s Statuses:");
                        for (String status : statusResp.getStatuses()) {
                            System.out.println("- " + status);
                        }
                    }
                } else {
                    System.out.println("\nFailed to retrieve the selected user's status.\n");
                }
            } else {
                System.out.println("\nCould not retrieve users with statuses. Try again.\n");
            }
        } catch (Exception e) {
            System.out.println("\nError fetching users with statuses: " + e.getMessage() + "\n");
        }
    }

    private static void deleteStatus(Scanner scanner) {
        System.out.println("\nFetching your statuses...");
    
        Timeout timeout = Timeout.create(java.time.Duration.ofSeconds(5));
        CompletableFuture<Object> statusFuture = FutureConverters
            .toJava(Patterns.ask(serverActor, new ViewStatus(username, username), timeout))
            .toCompletableFuture();
    
        try {
            Object response = statusFuture.get();
            if (response instanceof StatusResponse) {
                StatusResponse statusResponse = (StatusResponse) response;
    
                List<String> statuses = statusResponse.getStatuses();
                if (statuses.isEmpty()) {
                    System.out.println("\nNo statuses available.\n");
                    return;
                }
    
                System.out.println("\nYour statuses:");
                for (int i = 0; i < statuses.size(); i++) {
                    System.out.println((i + 1) + ". " + statuses.get(i));
                }
    
                System.out.print("\nEnter the number of the status to delete: ");
                String input = scanner.nextLine();
                int index = Integer.parseInt(input) - 1;
    
                if (index >= 0 && index < statuses.size()) {
                    String statusToDelete = statuses.get(index);
    
                    // Send the delete request
                    CompletableFuture<Object> deleteFuture = FutureConverters
                        .toJava(Patterns.ask(serverActor, new DeleteStatus(username, statusToDelete), timeout))
                        .toCompletableFuture();
    
                    Object deleteResponse = deleteFuture.get();
                    if (deleteResponse instanceof DeleteStatusResponse) {
                        DeleteStatusResponse deleteStatusResponse = (DeleteStatusResponse) deleteResponse;
                        System.out.println("\n[SYSTEM] " + deleteStatusResponse.getMessage() + "\n"); // Show success message
                    }
                } else {
                    System.out.println("\nInvalid selection. Please try again.\n");
                }
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage() + "\n");
        }
    }

}