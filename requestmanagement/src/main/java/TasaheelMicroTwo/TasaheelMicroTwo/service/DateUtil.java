package TasaheelMicroTwo.TasaheelMicroTwo.service;

import com.google.cloud.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm:ss z")
            .withZone(ZoneId.systemDefault());

    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return formatter.format(timestamp.toDate().toInstant());
    }
}
