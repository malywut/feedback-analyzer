package engineering.epic;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiModelName;
import dev.langchain4j.service.AiServices;
import engineering.epic.aiservices.FeedbackAnalyserAIService;
import engineering.epic.aiservices.FeedbackSplitterAIService;
import engineering.epic.databases.FeedbackEmbeddingStore;
import engineering.epic.datastorageobjects.AtomicFeedback;
import engineering.epic.datastorageobjects.FeedbackDTO;
import engineering.epic.datastorageobjects.UserFeedback;
import engineering.epic.databases.FeedbackDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FeedbackProcessor {
    @Inject
    FeedbackDatabase dbUtil;
    @Inject
    FeedbackEmbeddingStore embeddingStore;

    public UserFeedback processFeedback(FeedbackDTO dto) throws Exception {
        UserFeedback feedback = new UserFeedback(
                calculateBirthYear(dto.getAge()),
                dto.getCountry(),
                dto.getGender(),
                dto.getFeedback()
        );

        System.out.println("*********************************************************************************");
        System.out.println("****************  START PROCESSING  *********************************************");
        System.out.println("*********************************************************************************");
        System.out.println("Feedback received: " + feedback);
        // Call the feedbackAnalyzer to populate atomicFeedbackList
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAiModelName.GPT_4)
                .logRequests(true)
                .logResponses(true)
                .maxRetries(3)
                .build();

        FeedbackSplitterAIService splitter =
                AiServices.create(FeedbackSplitterAIService.class, model);

        List<String> coherentFeedbackParts = splitter.generateAtomicFeedbackComponents(feedback.getFeedback());

        FeedbackAnalyserAIService analyzer =
                AiServices.create(FeedbackAnalyserAIService.class, model);
        for(String coherentFeedbackPart : coherentFeedbackParts) {
            try {
                AtomicFeedback atomicFeedback = analyzer.generateAtomicFeedbackComponents(coherentFeedbackPart);
                feedback.addAtomicFeedback(atomicFeedback);
            } catch (Exception e) {
                System.out.println("Error processing feedback: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Retrying");
                try {
                    AtomicFeedback atomicFeedback = analyzer.generateAtomicFeedbackComponents(coherentFeedbackPart);
                    feedback.addAtomicFeedback(atomicFeedback);
                } catch (Exception e2) {
                    System.out.println("Second error processing feedback: " + e2.getMessage());
                    e2.printStackTrace();
                    System.out.println("Skipping");
                    // TODO add the feedback part nonetheless even if analysis failed
                }
            }
        }

        System.out.println("Feedback analyzed: " + feedback);

        // Persist feedback to the database
        persistFeedback(feedback);

        // Add feedback + embedding to the embedding store
        embeddingStore.addFeedback(feedback);

        return feedback;
    }

    private static int calculateBirthYear(int age) {
        return LocalDate.now().getYear() - age;
    }

    @Transactional
    public void persistFeedback(UserFeedback feedback) {
        dbUtil.executePost(feedback);
    }
}
