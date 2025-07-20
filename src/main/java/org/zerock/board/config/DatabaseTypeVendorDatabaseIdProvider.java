package org.zerock.board.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Custom DatabaseIdProvider for MyBatis that returns database IDs based on the current database type
 * rather than connecting to the database to determine the vendor.
 */
@Component
public class DatabaseTypeVendorDatabaseIdProvider implements DatabaseIdProvider {

    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        // Instead of connecting to the database, use the current database type
        DatabaseConfig.DatabaseType currentDatabase = DatabaseConfig.getCurrentDatabase();

        switch (currentDatabase) {
            case ORACLE:
                return "oracle";
            case MARIADB:
            default:
                return "mariadb";
        }
    }
}
