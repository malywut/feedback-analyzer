package engineering.epic;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiModelName;
import dev.langchain4j.service.AiServices;
import engineering.epic.aiservices.FeedbackChatAIService;

public class FeedbackChatAgent {
    ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(OpenAiModelName.GPT_3_5_TURBO)
            .logRequests(true)
            .logResponses(true)
            .maxRetries(3)
            .build();

    ChatMemoryProvider memoryProvider = memoryId -> MessageWindowChatMemory.builder()
            .id(memoryId)
            .maxMessages(15)
            .build();

    FeedbackChatAIService chatService = AiServices.builder(FeedbackChatAIService.class)
            .chatLanguageModel(model)
            .chatMemoryProvider(memoryProvider)
            .build();

    public String chat(int id, String userMessage) {
        return chatService.chat(id, userMessage);
    }


}
