package engineering.epic.endpoints;

import engineering.epic.datastorageobjects.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("api/dashboard")
public class DashboardController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DashboardResponse getDashboardData() {

        System.out.println("Received call to fetch dashboard data...");

        // TODO get the values from the database
        // TODO clean up the enum to have consequent capitalization
        List<AnalysisValue> analysis = List.of(
                new AnalysisValue("severity", Map.of("high", 14, "medium", 250, "low", 970)),
                new AnalysisValue("urgency", Map.of("high", 34, "medium", 150, "low", 870)),
                new AnalysisValue("impact", Map.of("high", 84, "medium", 230, "low", 970)),
                // TODO also for tags and categories and total number of atomic feedbacks, total number of feedbacks
                // new AnalysisValue("tag_name", yourMethodToGetTagValues()),
                // new AnalysisValue("category_name", yourMethodToGetCategoryValues())
                new AnalysisValue("tags", Map.of(
                        "inspiration", 15,
                        "tempo", 5,
                        "usefulness", 20,
                        "educational_value", 12,
                        "room_environment", 8,
                        "catering", 6,
                        "speakers", 14,
                        "timing", 11,
                        "readability", 7,
                        "complexity", 9
                )),
                new AnalysisValue("categories", Map.of(
                        "IdeaSuggestion", 10,
                        "Problem", 30,
                        "PositiveFeedback", 40,
                        "Undefined", 3
                )));


//        AtomicFeedback feedback1 = new AtomicFeedback();
//        feedback1.solutions = Collections.emptyList();
//        feedback1.tags = Arrays.asList(Tag.inspiration, Tag.usefulness, Tag.examples);
//        feedback1.category = Category.PositiveFeedback;
//        feedback1.urgency = 0;
//        feedback1.severity = 0;
//        feedback1.impact = 0;
//        feedback1.feedback = "Excellent workshop content, really very interesting to take home and continue to work on.";
//
//        AtomicFeedback feedback2 = new AtomicFeedback();
//        feedback2.solutions = Collections.emptyList();
//        feedback2.tags = Arrays.asList(Tag.tempo, Tag.complexity);
//        feedback2.category = Category.Problem;
//        feedback2.urgency = 70;
//        feedback2.severity = 60;
//        feedback2.impact = 50;
//        feedback2.feedback = "The tempo of the workshop was a little high, I could hardly follow by times.";
//
//        AtomicFeedback feedback3 = new AtomicFeedback();
//        feedback3.solutions = Collections.emptyList();
//        feedback3.tags = Collections.singletonList(Tag.catering);
//        feedback3.category = Category.Problem;
//        feedback3.urgency = 60;
//        feedback3.severity = 20;
//        feedback3.impact = 100;
//        feedback3.feedback = "The food was really crap.";

        AtomicFeedback[] feedbacks = new AtomicFeedback[]{
                generateAtomicFeedback(Arrays.asList(Tag.inspiration, Tag.usefulness, Tag.examples), Category.PositiveFeedback, 0, 0, 0, "Excellent workshop content, really very interesting to take home and continue to work on."),
                generateAtomicFeedback(Arrays.asList(Tag.tempo, Tag.complexity), Category.Problem, 70, 60, 50, "The tempo of the workshop was a little high, I could hardly follow by times."),
                generateAtomicFeedback(Collections.singletonList(Tag.catering), Category.Problem, 60, 20, 100, "The food was really crap."),
                generateAtomicFeedback(Arrays.asList(Tag.educational_value), Category.PositiveFeedback, 0, 0, 0, "The practical examples were extremely helpful, great learning experience."),
                generateAtomicFeedback(Arrays.asList(Tag.room_environment), Category.Problem, 40, 50, 70, "The room was too cold, and the seating was uncomfortable."),
                generateAtomicFeedback(Arrays.asList(Tag.speakers, Tag.timing), Category.IdeaSuggestion, 30, 20, 60, "Speakers should have more time for Q&A sessions."),
                generateAtomicFeedback(Arrays.asList(Tag.boredom, Tag.timing), Category.Problem, 50, 40, 80, "Some sessions were too long, leading to loss of focus."),
                generateAtomicFeedback(Arrays.asList(Tag.readability, Tag.examples), Category.IdeaSuggestion, 20, 10, 30, "Workshop materials could be more engaging with interactive content."),
                generateAtomicFeedback(Arrays.asList(Tag.usefulness, Tag.educational_value), Category.PositiveFeedback, 0, 0, 0, "Loved the hands-on part, it was very educational."),
                generateAtomicFeedback(Arrays.asList(Tag.complexity), Category.Problem, 80, 70, 90, "Some concepts were too complex for beginners."),
                generateAtomicFeedback(Arrays.asList(Tag.catering), Category.Problem, 60, 10, 30, "Vegetarian options were limited."),
                generateAtomicFeedback(Arrays.asList(Tag.speakers), Category.PositiveFeedback, 0, 0, 0, "Speakers were knowledgeable and engaging."),
                generateAtomicFeedback(Arrays.asList(Tag.timing), Category.IdeaSuggestion, 25, 15, 50, "More breaks between sessions would be appreciated."),
                generateAtomicFeedback(Arrays.asList(Tag.room_environment), Category.Problem, 55, 45, 75, "The projector was not clear, making it hard to see the slides."),
                generateAtomicFeedback(Arrays.asList(Tag.inspiration), Category.PositiveFeedback, 0, 0, 0, "I am inspired to apply what I learned in my projects."),
                generateAtomicFeedback(Arrays.asList(Tag.tempo, Tag.complexity), Category.Problem, 65, 55, 85, "The fast pace made it difficult to grasp complex topics."),
                generateAtomicFeedback(Arrays.asList(Tag.educational_value), Category.PositiveFeedback, 0, 0, 0, "This workshop has significantly improved my understanding."),
                generateAtomicFeedback(Arrays.asList(Tag.catering), Category.IdeaSuggestion, 35, 25, 45, "Would love to see more healthy food options next time."),
                generateAtomicFeedback(Arrays.asList(Tag.boredom), Category.Problem, 45, 35, 55, "Some parts of the workshop could be made more interactive."),
                generateAtomicFeedback(Arrays.asList(Tag.readability, Tag.educational_value), Category.IdeaSuggestion, 20, 30, 40, "Suggest providing a glossary for technical terms used."),
                generateAtomicFeedback(Arrays.asList(Tag.timing, Tag.speakers), Category.Problem, 75, 65, 85, "Insufficient time allocated for some of the more interesting topics."),
                generateAtomicFeedback(Arrays.asList(Tag.complexity), Category.IdeaSuggestion, 10, 50, 60, "Consider beginner and advanced tracks for future workshops."),
                generateAtomicFeedback(Arrays.asList(Tag.inspiration, Tag.usefulness), Category.PositiveFeedback, 0, 0, 0, "The workshop content was practical and immediately applicable."),
                generateAtomicFeedback(Arrays.asList(Tag.room_environment), Category.Problem, 50, 60, 70, "Acoustics in the room made it hard to hear the speakers.")
        };

        //List<AtomicFeedback> feedbacks = fetchFeedbacks(); // TODO Implement this method to fetch feedbacks

        DashboardResponse response = new DashboardResponse(analysis, Arrays.stream(feedbacks).toList());

        return response;
    }

    public AtomicFeedback generateAtomicFeedback(List<Tag> tags, Category category,
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