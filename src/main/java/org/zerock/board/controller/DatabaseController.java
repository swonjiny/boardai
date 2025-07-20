package org.zerock.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.board.config.DatabaseConfig;
import org.zerock.board.service.DatabaseService;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for database operations.
 */
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Get the current database type.
     *
     * @return ResponseEntity with the current database type
     */
    @GetMapping("/type")
    public ResponseEntity<Map<String, String>> getCurrentDatabaseType() {
        DatabaseConfig.DatabaseType currentType = databaseService.getCurrentDatabaseType();

        Map<String, String> response = new HashMap<>();
        response.put("databaseType", currentType.name());

        return ResponseEntity.ok(response);
    }

    /**
     * Switch to the specified database type.
     *
     * @param databaseType The database type to switch to (MARIADB or ORACLE)
     * @return ResponseEntity with the new database type
     */
    @PostMapping("/switch")
    public ResponseEntity<?> switchDatabase(@RequestParam String databaseType) {
        // Validate the database type
        if (!databaseService.isValidDatabaseType(databaseType)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid database type. Valid types are: MARIADB, ORACLE");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Switch to the specified database type
        DatabaseConfig.DatabaseType newType = databaseService.switchDatabase(
            DatabaseConfig.DatabaseType.valueOf(databaseType.toUpperCase())
        );

        Map<String, String> response = new HashMap<>();
        response.put("databaseType", newType.name());
        response.put("message", "Successfully switched to " + newType.name());

        return ResponseEntity.ok(response);
    }
}
