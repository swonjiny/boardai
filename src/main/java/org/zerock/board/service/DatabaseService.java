package org.zerock.board.service;

import org.springframework.stereotype.Service;
import org.zerock.board.config.DatabaseConfig;

/**
 * Service for managing database connections and switching between database types.
 */
@Service
public class DatabaseService {

    /**
     * Get the current database type.
     *
     * @return The current database type (MARIADB or ORACLE)
     */
    public DatabaseConfig.DatabaseType getCurrentDatabaseType() {
        return DatabaseConfig.getCurrentDatabase();
    }

    /**
     * Switch to the specified database type.
     *
     * @param databaseType The database type to switch to (MARIADB or ORACLE)
     * @return The new database type
     */
    public DatabaseConfig.DatabaseType switchDatabase(DatabaseConfig.DatabaseType databaseType) {
        DatabaseConfig.setCurrentDatabase(databaseType);
        return DatabaseConfig.getCurrentDatabase();
    }

    /**
     * Check if the specified database type is valid.
     *
     * @param databaseType The database type to check
     * @return true if the database type is valid, false otherwise
     */
    public boolean isValidDatabaseType(String databaseType) {
        try {
            DatabaseConfig.DatabaseType.valueOf(databaseType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
