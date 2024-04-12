package engineering.epic;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static engineering.epic.util.DbUtil.executeSqlScript;

@Singleton
public class StartupService {
    @ConfigProperty(name = "app.database.reset", defaultValue = "false")
    boolean resetDatabase;
    @ConfigProperty(name = "app.database.prepopulate", defaultValue = "false")
    boolean prepopulate;

    public void onStart(@Observes StartupEvent ev) {
        try {
            // Wipe the database if flag is set in application.properties
            if (resetDatabase) {
                executeSqlScript("db/sql/drop_tables.sql");
                System.out.println("Database tables dropped successfully.");
            }
        } catch (Exception e) {
            System.out.println("Failed to drop tables: " + e.getMessage());
        }

        try {
            // Initialize the SQLite database if it wasn't already done
            executeSqlScript("db/sql/create_tables.sql");
            System.out.println("Database tables created successfully.");
        } catch (Exception e) {
            System.out.println("Failed to create tables: " + e.getMessage());
        }

        try {
            executeSqlScript("db/sql/create_tags.sql");
            System.out.println("Tags inserted successfully.");
        } catch (Exception e) {
            System.out.println("Failed to insert tags: " + e.getMessage());
        }

        try {
            // Populate the database with demo data if flag is set in application.properties
            if (prepopulate) {
                executeSqlScript("db/sql/populate_with_demo_data.sql");
                System.out.println("Database populated with demo data successfully.");
            }
        } catch (Exception e) {
            System.out.println("Failed to populate database with demo data: " + e.getMessage());
        }
    }
}
