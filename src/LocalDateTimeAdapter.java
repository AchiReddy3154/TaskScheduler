import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple adapter for LocalDateTime serialization/deserialization
 * This is a basic implementation for JSON handling without external libraries
 */
public class LocalDateTimeAdapter {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Converts LocalDateTime to String for JSON storage
     */
    public static String serialize(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }
    
    /**
     * Converts String back to LocalDateTime from JSON
     */
    public static LocalDateTime deserialize(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, FORMATTER);
        } catch (Exception e) {
            // Try alternative format
            try {
                return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e2) {
                System.err.println("Failed to parse date: " + dateTimeString);
                return LocalDateTime.now();
            }
        }
    }
}
