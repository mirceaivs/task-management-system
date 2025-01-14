package javaweb.task_management_system.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DatabaseConnectionChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionChecker.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseConnectionChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Execute a test query to verify database connectivity
            Integer result = jdbcTemplate.queryForObject("SELECT 1 + 1", Integer.class);
            logger.info("Database connected successfully. Test query result: {}", result);
        } catch (Exception e) {
            // Log the failure and throw an exception to stop the app
            logger.error("Database connection failed: {}", e.getMessage());
            throw new IllegalStateException("Cannot connect to the database. Application startup aborted.", e);
        }
    }
}
