package engineering.epic.endpoints;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiModelName;
import dev.langchain4j.service.AiServices;
import engineering.epic.aiservices.FeedbackChatAIService;
import io.quarkiverse.langchain4j.ChatMemoryRemover;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

@ServerEndpoint(value = "/api/chat")
public class ChatSocket {

    private static final Logger LOG = Logger.getLogger(ChatSocket.class);

    private final ManagedExecutor managedExecutor;

    FeedbackChatAIService chatService;

    public ChatSocket(ManagedExecutor managedExecutor) {
        this.managedExecutor = managedExecutor;
    }

    @OnOpen
    public void onOpen(Session session) {
        session.getUserProperties().put("sessionId", UUID.randomUUID().toString());
        System.out.println("Session opened, ID: " + session.getUserProperties().get("sessionId"));

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

        chatService = AiServices.builder(FeedbackChatAIService.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(memoryProvider)
                .build();
    }

    @OnMessage
    public void onMessage(Session session, String userMessage) throws Exception {
        System.out.println("Received message: " + userMessage);
        // retrieve user ID
        final String sessionId = (String) session.getUserProperties().get("sessionId");

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        // we need to use a worker thread because OnMessage always runs on the event loop
        managedExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    session.getBasicRemote().sendText("[User]: " + userMessage);
                    String response = chatService.chat(sessionId, userMessage);
                    session.getBasicRemote().sendText("[AI Assistant]: " + response);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        });
    }

    @OnClose
    void onClose(Session session) {
        final String sessionId = (String) session.getUserProperties().get("sessionId");
        ChatMemoryRemover.remove(chatService, sessionId);
        LOG.info("Session closed, ID: " + sessionId);
    }


}
