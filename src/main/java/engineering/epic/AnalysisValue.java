package engineering.epic;

import java.util.Map;

public class AnalysisValue {
    private String name;
    private Map<String, Integer> values;

    // Constructor
    public AnalysisValue(String name, Map<String, Integer> values) {
        this.name = name;
        this.values = values;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public void setValues(Map<String, Integer> values) {
        this.values = values;
    }
}
