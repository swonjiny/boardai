package org.zerock.board.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfig {

    // Database type enum
    public enum DatabaseType {
        MARIADB, ORACLE
    }

    // ThreadLocal to store the current database type
    private static final ThreadLocal<DatabaseType> currentDatabase =
            ThreadLocal.withInitial(() -> DatabaseType.MARIADB);

    // Method to get the current database type
    public static DatabaseType getCurrentDatabase() {
        return currentDatabase.get();
    }

    // Method to set the current database type
    public static void setCurrentDatabase(DatabaseType databaseType) {
        currentDatabase.set(databaseType);
    }

    // MariaDB DataSource
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mariadb")
    public DataSource mariadbDataSource() {
        return DataSourceBuilder.create().build();
    }

    // Oracle DataSource properties
    @Value("${spring.datasource.oracle.jdbc-url}")
    private String oracleJdbcUrl;

    @Value("${spring.datasource.oracle.username}")
    private String oracleUsername;

    @Value("${spring.datasource.oracle.password}")
    private String oraclePassword;

    @Value("${spring.datasource.oracle.driver-class-name}")
    private String oracleDriverClassName;

    // Oracle DataSource
    @Bean
    @org.springframework.context.annotation.Lazy
    public DataSource oracleDataSource() {
        // Use DataSourceBuilder to create the actual DataSource
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.url(oracleJdbcUrl);
        builder.username(oracleUsername);
        builder.password(oraclePassword);
        builder.driverClassName(oracleDriverClassName);

        // Wrap in LazyConnectionDataSourceProxy to defer connection until actually used
        return new LazyConnectionDataSourceProxy(builder.build());
    }

    // Routing DataSource that switches between MariaDB and Oracle
    @Bean
    @Primary
    public DataSource routingDataSource(
            @Qualifier("mariadbDataSource") DataSource mariadbDataSource,
            @Qualifier("oracleDataSource") @org.springframework.context.annotation.Lazy DataSource oracleDataSource) {

        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return getCurrentDatabase();
            }

            @Override
            protected DataSource determineTargetDataSource() {
                Object lookupKey = determineCurrentLookupKey();
                if (lookupKey == DatabaseType.MARIADB) {
                    return mariadbDataSource;
                } else {
                    // Only access oracleDataSource when explicitly requested
                    return oracleDataSource;
                }
            }
        };

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DatabaseType.MARIADB, mariadbDataSource);
        // We don't put Oracle in the map to avoid initialization
        // It will be accessed directly in determineTargetDataSource

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(mariadbDataSource); // Default to MariaDB

        return routingDataSource;
    }
}
