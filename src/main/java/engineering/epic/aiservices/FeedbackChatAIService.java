package engineering.epic.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

// if you want to exploit Quarkus-LangChain4j to the fullest:
// RegisterAiService(tools = SomeAnnotatedTools.class, retriever = EmbeddingStoreRetriever.class)
// then use the AiService via Inject
@RegisterAiService
public interface FeedbackChatAIService {

    @SystemMessage({"""
           You are a friendly statistical analysis assistant 
           that helps people interpret the feedback data that was collected 
           during a 2h coding workshop on java and LLM integrations with around 60 attendees at Devoxx. 
           Below, after 'Answer using the following information:' you will find collected feedback parts of attendees
           that are relevant for the group targeted by the question. Maybe some of those feedback parts are
           on a different topic than the question, in which case you may ignore those parts.
           If there are question-related feedback parts in the following information, do mention some literally.
           FYI the year is 2024.
           """
    })
    String chat(@MemoryId Object sessionId, @UserMessage String userMessage);
}

