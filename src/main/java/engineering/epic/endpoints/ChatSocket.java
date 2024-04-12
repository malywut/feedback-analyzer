package engineering.epic.endpoints;

import engineering.epic.aiservices.FeedbackChatAIService;
import io.quarkiverse.langchain4j.ChatMemoryRemover;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

@ServerEndpoint(value = "/api/chat")
public class ChatSocket {

    private static final Logger LOG = Logger.getLogger(ChatSocket.class);

    private final FeedbackChatAIService agent;
    private final ManagedExecutor managedExecutor;

    public ChatSocket(FeedbackChatAIService agent, ManagedExecutor managedExecutor) {
        this.agent = agent;
        this.managedExecutor = managedExecutor;
    }

    @OnMessage
    public void onMessage(Session session, String userMessage) throws Exception {
        System.out.println("Received message: " + userMessage);

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        // we need to use a worker thread because OnMessage always runs on the event loop
        managedExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    session.getBasicRemote().sendText("[User]: " + userMessage);
                    // TODO restore when key issue figured out
                 //   session.getBasicRemote().sendText("[AI Assistant]: " + agent.chat(session, userMessage));
                    session.getBasicRemote().sendText("[AI Assistant]: " + "dummy answer while we figure out the key issue");
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
        ChatMemoryRemover.remove(agent, session);
    }


}
