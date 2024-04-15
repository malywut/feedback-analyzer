package engineering.epic.endpoints;

import engineering.epic.datastorageobjects.FeedbackDTO;
import engineering.epic.FeedbackProcessorAgent;
import engineering.epic.datastorageobjects.UserFeedback;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("api/feedbackprocessor")
public class FeedbackController {


    @Inject
    FeedbackProcessorAgent fpa;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitFeedback(FeedbackDTO feedbackDTO) {
        System.out.println("Received feedback: " + feedbackDTO.getFeedback());

        try {
            // Cut the feedback into atomic feedbacks
            UserFeedback processedFeedback = fpa.processFeedback(feedbackDTO);
            // Return the processed feedback as JSON
            return Response.ok(processedFeedback.getAtomicFeedbacks()).build();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing feedback: " + e.getMessage());
            // If an error occurs during processing, return an error response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error occurred while processing feedback: " + e.getMessage())
                    .build();
        }
    }
}
