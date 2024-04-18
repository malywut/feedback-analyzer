package engineering.epic.aiservices;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

import java.util.List;

@RegisterAiService
public interface FeedbackSplitterAIService {
    @SystemMessage("""
            Split the user feedback into coherent parts of literal user text that treat a similar topic, that can then be addressed separately.
            If the user treats the same topic in multiple parts of the feedback, make sure to group them together.
            """)
    public List<String> generateAtomicFeedbackComponents(String feedback);
}
