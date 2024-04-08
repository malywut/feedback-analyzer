package engineering.epic;

import java.util.List;

public class DashboardResponse {
    private List<AnalysisValue> analysis;
    private List<AtomicFeedback> feedbacks;

    // Constructor
    public DashboardResponse(List<AnalysisValue> analysis, List<AtomicFeedback> feedbacks) {
        this.analysis = analysis;
        this.feedbacks = feedbacks;
    }

    // Getters and Setters
    public List<AnalysisValue> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List<AnalysisValue> analysis) {
        this.analysis = analysis;
    }

    public List<AtomicFeedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<AtomicFeedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
