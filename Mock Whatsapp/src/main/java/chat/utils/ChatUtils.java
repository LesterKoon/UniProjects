package chat.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatUtils {

    // Formats a message with a timestamp
    public static String formatMessage(String sender, String messageContent) {
        String timestamp = getCurrentTimestamp();
        return "[" + timestamp + "] " + sender + ": " + messageContent;
    }

    // Gets the current timestamp in a readable format
    public static String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    // Validates if a username is valid (alphanumeric, 3-15 characters)
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,15}$");
    }

    // Validates if a message is valid (non-empty, <= 500 characters)
    public static boolean isValidMessage(String message) {
        return message != null && !message.trim().isEmpty() && message.length() <= 500;
    }
}
