package me.findthepeach.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "Service health monitoring endpoints")
public class HealthController {
    private final DataSource dataSource;

    @Operation(summary = "Check database connection health")
    @GetMapping("/health")
    public String healthCheckConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery("SELECT 1");
            }
            return "User Service Healthy!";
        }
    }
}
