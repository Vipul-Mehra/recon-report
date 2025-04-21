    package com.model;

    import jakarta.annotation.PostConstruct;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.PersistenceContext;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.boot.SpringApplication;
    // Remove @SpringBootApplication
    import org.springframework.boot.autoconfigure.domain.EntityScan;
    import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Component;

    @Component // Use @Component instead if it's not the main app
    @EntityScan(basePackages = "com.entity")
    @EnableJpaRepositories(basePackages = "com.repository")
    public class DatabaseCreationApplication implements CommandLineRunner {

        @Value("${spring.datasource.url}")
        private String jdbcUrl;

        @Value("${spring.datasource.username}")
        private String username;

        @Value("${spring.datasource.password}")
        private String password;

        @PersistenceContext
        private EntityManager entityManager;

        private final JdbcTemplate jdbcTemplate;

        public DatabaseCreationApplication(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        public static void main(String[] args) {
            SpringApplication.run(DatabaseCreationApplication.class, args);
        }

        @Override
        public void run(String... args) throws Exception {
            createDatabaseAndTables();
        }

        @Transactional
        public void createDatabaseAndTables() {
            String databaseName = extractDatabaseNameFromUrl();

            if (databaseName != null && !databaseExists(databaseName)) {
                System.out.println("Creating database: " + databaseName);
                jdbcTemplate.execute("CREATE DATABASE " + databaseName);
                System.out.println("Database created successfully.");
            } else if (databaseName != null) {
                System.out.println("Database '" + databaseName + "' already exists.");
            } else {
                System.err.println("Could not extract database name from JDBC URL.");
                return;
            }

            // Hibernate will now create the tables based on your entities
            System.out.println("Hibernate will now create the tables based on entities...");
        }

        private String extractDatabaseNameFromUrl() {
            String lowerCaseUrl = jdbcUrl.toLowerCase();
            if (lowerCaseUrl.startsWith("jdbc:mysql://")) {
                int start = lowerCaseUrl.lastIndexOf('/') + 1;
                int end = lowerCaseUrl.contains("?") ? lowerCaseUrl.indexOf('?', start) : lowerCaseUrl.length();
                if (start > 0 && start < end) {
                    return jdbcUrl.substring(start, end);
                }
            }
            return null;
        }

        private boolean databaseExists(String databaseName) {
            try {
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?",
                        Integer.class,
                        databaseName
                );
                return count != null && count > 0;
            } catch (Exception e) {
                System.err.println("Error checking if database exists: " + e.getMessage());
                return false;
            }
        }

        @PostConstruct
        public void logConfiguration() {
            System.out.println("JDBC URL: " + jdbcUrl);
            System.out.println("Username: " + username);
            System.out.println("Database creation process started...");
        }
    }