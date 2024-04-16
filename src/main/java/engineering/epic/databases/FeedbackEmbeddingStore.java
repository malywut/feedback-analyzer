package engineering.epic.databases;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FeedbackEmbeddingStore{
    EmbeddingStore<TextSegment> embeddingStore;
    EmbeddingModel embeddingModel;

    public FeedbackEmbeddingStore(){
        embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore(){
        return embeddingStore;
    }

    public void populateFromDatabase(FeedbackDatabase dbUtil) {
        TextSegment segment1 = TextSegment.from("the food tastes like cardboard");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("The food had really no taste at all");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);

        TextSegment segment3 = TextSegment.from("Especially the example where you could fetch info from the langchain4j documentation was super cool");
        Embedding embedding3 = embeddingModel.embed(segment3).content();
        embeddingStore.add(embedding3, segment3);
    }
}
