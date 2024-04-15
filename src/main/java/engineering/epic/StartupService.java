package engineering.epic;

import engineering.epic.util.DbUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class StartupService {
    @ConfigProperty(name = "app.database.reset", defaultValue = "false")
    boolean resetDatabase;
    @ConfigProperty(name = "app.database.prepopulate", defaultValue = "false")
    boolean prepopulate;

    @Inject
    DbUtil dbUtil;

    public void onStart(@Observes StartupEvent ev) {
        try {
            // Wipe the database if flag is set in application.properties
            if (resetDatabase) {
                dbUtil.executeSqlScript("db/sql/drop_tables.sql");
                System.out.println("Database tables dropped successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to drop tables: " + e.getMessage());
        }

        try {
            // Initialize the SQLite database if it wasn't already done
            dbUtil.executeSqlScript("db/sql/create_tables.sql");
            System.out.println("Database tables created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create tables: " + e.getMessage());
        }

        try {
            dbUtil.executeSqlScript("db/sql/create_tags.sql");
            System.out.println("Tags inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to insert tags: " + e.getMessage());
        }

        try {
            // Populate the database with demo data if flag is set in application.properties
            if (prepopulate) {
                dbUtil.executeSqlScript("db/sql/populate_with_demo_data.sql");
                System.out.println("Database populated with demo data successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to populate database with demo data: " + e.getMessage());
        }
    }
}
