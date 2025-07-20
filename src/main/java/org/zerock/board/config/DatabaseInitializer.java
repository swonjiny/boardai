package org.zerock.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DatabaseInitializer {

    @Value("${spring.database.type:mariadb}")
    private String databaseType;

    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            // Set the current database type based on configuration
            DatabaseConfig.DatabaseType dbType = "oracle".equalsIgnoreCase(databaseType)
                ? DatabaseConfig.DatabaseType.ORACLE
                : DatabaseConfig.DatabaseType.MARIADB;

            DatabaseConfig.setCurrentDatabase(dbType);

            // Load the appropriate schema file
            String schemaFile = "oracle".equalsIgnoreCase(databaseType)
                ? "schema-oracle.sql"
                : "schema.sql";

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource(schemaFile));
            populator.execute(dataSource);

            System.out.println("Database initialized with " + databaseType + " schema");
        };
    }
}
