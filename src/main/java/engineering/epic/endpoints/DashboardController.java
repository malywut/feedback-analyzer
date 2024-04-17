package engineering.epic.endpoints;

import engineering.epic.datastorageobjects.*;
import engineering.epic.databases.FeedbackDatabase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("api/dashboard")
public class DashboardController {
    @Inject
    FeedbackDatabase dbUtil;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DashboardResponse getDashboardData() {

        System.out.println("Received call to fetch dashboard data...");

        // severity, urgency, impact: high = >75, medium = 25-75, low = <25
        Integer sevHigh, sevMid, sevLow, urgHigh, urgMid, urgLow, impHigh, impMid, impLow;
        sevHigh = dbUtil.loadIntHigh("severity");
        sevMid = dbUtil.loadIntMid("severity");
        sevLow = dbUtil.loadIntLow("severity");
        urgHigh = dbUtil.loadIntHigh("urgency");
        urgMid = dbUtil.loadIntMid("urgency");
        urgLow = dbUtil.loadIntLow("urgency");
        impHigh = dbUtil.loadIntHigh("impact");
        impMid = dbUtil.loadIntMid("impact");
        impLow = dbUtil.loadIntLow("impact");

        Map<String, Integer> tagCounts = dbUtil.getTagCounts();

        List<AnalysisValue> analysis = List.of(
                new AnalysisValue("tags", tagCounts),
                new AnalysisValue("categories", dbUtil.getCategoryCounts()),
                new AnalysisValue("severity", Map.of("high", sevHigh, "medium", sevMid, "low", sevLow)),
                new AnalysisValue("urgency", Map.of("high", urgHigh, "medium", urgMid, "low", urgLow)),
                new AnalysisValue("impact", Map.of("high", impHigh, "medium", impMid, "low", impLow))
        );


        List<AtomicFeedback> feedbacks = dbUtil.fetchFeedbacks();

        DashboardResponse response = new DashboardResponse(analysis, feedbacks);

        return response;
    }

    public static AtomicFeedback generateAtomicFeedback(List<Tag> tags, Category category,
                                                 int urgency, int severity, int impact, String feedback) {
        AtomicFeedback result = new AtomicFeedback();
        result.tags = tags;
        result.category = category;
        result.urgency = urgency;
        result.severity = severity;
        result.impact = impact;
        result.feedback = feedback;
        return result;
    }
}