package scanmycar.model.repository;

import java.sql.Connection;

public class ConnectionManagerTestHelper {
    public static void setConnection(Connection testConnection) {
        try {
            var field = ConnectionManager.class.getDeclaredField("connection");
            field.setAccessible(true);
            field.set(null, testConnection);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de setter la connexion test", e);
        }
    }
}
