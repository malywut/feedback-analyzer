package engineering.epic;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/feedbackprocessor")
public class FeedbackController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitFeedback(FeedbackDTO feedbackDTO) {
        try {
            String feedbackAnalysis = FeedbackProcessorAgent.processFeedback(feedbackDTO);
            // If processing is successful, return a success response
            return Response.ok().entity(feedbackAnalysis).build();
        } catch (Exception e) {
            System.out.println("Error processing feedback: " + e.getMessage());
            // If an error occurs during processing, return an error response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error occurred while processing feedback: " + e.getMessage())
                    .build();
        }
    }
}
