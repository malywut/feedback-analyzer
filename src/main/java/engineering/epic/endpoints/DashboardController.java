package engineering.epic.endpoints;

import engineering.epic.datastorageobjects.*;
import engineering.epic.util.DbUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("api/dashboard")
public class DashboardController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DashboardResponse getDashboardData() {

        System.out.println("Received call to fetch dashboard data...");

        // severity, urgency, impact: high = >75, medium = 25-75, low = <25
        Integer sevHigh, sevMid, sevLow, urgHigh, urgMid, urgLow, impHigh, impMid, impLow;
        sevHigh = DbUtil.loadIntHigh("severity");
        sevMid = DbUtil.loadIntMid("severity");
        sevLow = DbUtil.loadIntLow("severity");
        urgHigh = DbUtil.loadIntHigh("urgency");
        urgMid = DbUtil.loadIntMid("urgency");
        urgLow = DbUtil.loadIntLow("urgency");
        impHigh = DbUtil.loadIntHigh("impact");
        impMid = DbUtil.loadIntMid("impact");
        impLow = DbUtil.loadIntLow("impact");

        Map<String, Integer> tagCounts = DbUtil.getTagCounts();

        // TODO make upper casing use coherent
        List<AnalysisValue> analysis = List.of(
                new AnalysisValue("severity", Map.of("high", sevHigh, "medium", sevMid, "low", sevLow)),
                new AnalysisValue("urgency", Map.of("high", urgHigh, "medium", urgMid, "low", urgLow)),
                new AnalysisValue("impact", Map.of("high", impHigh, "medium", impMid, "low", impLow)),
                new AnalysisValue("tags", tagCounts),
                new AnalysisValue("categories", DbUtil.getCategoryCounts()));

        List<AtomicFeedback> feedbacks = DbUtil.fetchFeedbacks();

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