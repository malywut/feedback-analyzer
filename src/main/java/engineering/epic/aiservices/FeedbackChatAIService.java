package engineering.epic.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

// TODO for registering tools and rag: RegisterAiService(tools = BookingTools.class, retriever = EmbeddingStoreRetriever.class)
@RegisterAiService
public interface FeedbackChatAIService {

    @SystemMessage({"""
           You are a friendly statistical analysis assistant that helps people making sense
           out of the feedback data that was collected during 
            """
    })
    String chat(@MemoryId Object session, @UserMessage String userMessage);
}

