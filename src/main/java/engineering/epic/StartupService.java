package engineering.epic;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

import static engineering.epic.util.DbUtil.executeSqlScript;

@Singleton
public class StartupService {

    public void onStart(@Observes StartupEvent ev) {
        executeSqlScript("db/sql/create_tables.sql");
        executeSqlScript("db/sql/create_tags.sql");
    }
}
