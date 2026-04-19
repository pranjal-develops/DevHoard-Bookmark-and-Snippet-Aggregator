package com.devhoard.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseHeartbeat implements InitializingBean {

    private final DataSource dataSource;

    public DatabaseHeartbeat(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("💓 [HEARTBEAT] Testing Database Connection...");
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                System.out.println("✅ [HEARTBEAT] DATABASE IS ALIVE AND REACHABLE!");
            } else {
                System.out.println("⚠️ [HEARTBEAT] Connection established but reported as INVALID.");
            }
        } catch (Exception e) {
            System.err.println("❌ [HEARTBEAT] DATABASE CONNECTION FAILED!");
            System.err.println("   Reason: " + e.getMessage());
            // We don't throw an exception here because we want the standard Spring Boot
            // error messages to follow for more detail, but this gives a LOUD warning
            // first.
        }
    }
}
