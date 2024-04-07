package engineering.epic;

import dev.langchain4j.service.SystemMessage;

import java.util.List;

public interface FeedbackSplitterAIService {
    @SystemMessage("""
            Split the user feedback into coherent parts that treat a similar topic, that can then be addressed separately.
            If the user treats the same topic in multiple parts of the feedback, make sure to group them together.
            """)
    public List<String> generateAtomicFeedbackComponents(String feedback);
}
