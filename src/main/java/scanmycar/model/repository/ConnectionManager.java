package scanmycar.model.repository;


import scanmycar.model.repository.utilRepository.RepositoryException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class ConnectionManager {
    private static Connection connection;
    private static Properties properties = null;

    private static Properties loadProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream input = ConnectionManager.class.getClassLoader()
                    .getResourceAsStream("database.properties")) {
                properties.load(input);
            } catch (IOException e ){
                throw new RepositoryException("Properties illisible", e);
            }
        }
        return properties;
    }

    static Connection getConnection() {
        if (connection == null) {
            try {
                loadProperties();
                Properties config = loadProperties();
                String url = config.getProperty("db.url");
                String user = config.getProperty("db.user");
                String password = config.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
                // Activation foreing key constraints
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }

            } catch (SQLException e) {
                throw new RepositoryException("Connexion impossible", e);
            }
        }
        return connection;
    }

    static void close()  {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Fermeture impossible", e);
        }
    }
}

