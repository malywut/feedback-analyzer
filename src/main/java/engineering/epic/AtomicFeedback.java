package engineering.epic;
import dev.langchain4j.model.output.structured.Description;

import java.util.List;

// Enum for feedback categories
enum Category {
    IdeaSuggestion,
    Problem,
    PositiveFeedback,
    Undefined
}

enum Tag {
    examples,
    inspiration,
    tempo,
    usefulness,
    educational_value,
    room_environment,
    catering,
    speakers,
    timing,
    readability,
    boredom,
    complexity
}

public class AtomicFeedback {
    @Description("to be left empty")
    public List<String> solutions;
    @Description("""
            all relevant topic tags, picked strictly from this list:
                examples,
                inspiration,
                tempo,
                usefulness,
                educational_value,
                room_environment,
                catering,
                speakers,
                timing,
                readability,
                boredom,
                complexity
            """)
    public List<Tag> tags;
    @Description("best applicable feedback category")
    public Category category;
    @Description("number: % of urgency, ranging from 0 (no urgence whatsoever) to 100 (needs fixing in this hour). should be " +
            "0 for positive feedback")
    public int urgency;
    @Description("number, % of severity, ranging from 0 (no problem whatsoever) to 100 (person(s) will die if this is not " +
            "fixed). should be 0 for positive feedback")
    public int severity;
    @Description("number, % of people in the scope that are estimated to be affected by this same problem or solution.")
    public int impact;
    @Description("the literal part(s) of the original UserFeedback that is relevant for this AtomicFeedback part")
    public String feedback;

    public String prettyPrint() {
        return "\nAtomicFeedback{" +
                "\n\tsolutions=" + solutions +
                ", \n\ttags=" + tags +
                ", \n\tcategory=" + category +
                ", \n\turgency=" + urgency +
                ", \n\tseverity=" + severity +
                ", \n\timpact=" + impact +
                ", \n\tfeedback='" + feedback + '\'' +
                "'\n}'";
    }

//
//    // Getters and setters for each field
//    public List<String> getSolutions() {
//        return solutions;
//    }
//
//    public void setSolutions(List<String> solutions) {
//        this.solutions = solutions;
//    }
//
//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
//
//    public int getUrgency() {
//        return urgency;
//    }
//
//    public void setUrgency(int urgency) {
//        this.urgency = urgency;
//    }
//
//    public int getSeverity() {
//        return severity;
//    }
//
//    public void setSeverity(int severity) {
//        this.severity = severity;
//    }
//
//    public int getImpact() {
//        return impact;
//    }
//
//    public void setImpact(int impact) {
//        this.impact = impact;
//    }
//
//    public String getFeedback() {
//        return feedback;
//    }
//
//    public void setFeedback(String feedback) {
//        this.feedback = feedback;
//    }
}
