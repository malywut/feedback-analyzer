package engineering.epic.databases;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.model.embedding.EmbeddingModel;
import engineering.epic.datastorageobjects.AtomicFeedback;
import engineering.epic.datastorageobjects.Tag;
import engineering.epic.datastorageobjects.UserFeedback;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class FeedbackEmbeddingStore {
    EmbeddingStore<TextSegment> embeddingStore;
    EmbeddingModel embeddingModel;

    public FeedbackEmbeddingStore() {
        embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        return embeddingStore;
    }

    public void populateFromDatabase(FeedbackDatabase dbUtil) {
        List<FeedbackDatabase.ExtendedAtomicFeedback> fullAtomicFeedbacks = dbUtil.fetchExtendedFeedbacks();
        for (FeedbackDatabase.ExtendedAtomicFeedback feedback : fullAtomicFeedbacks) {

            TextSegment segment = TextSegment.from(feedback.getFeedback(), Metadata.from(feedback.getMetaInfo()));
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }
    }

    public void addFeedback(UserFeedback feedback) {
        if (feedback == null) {
            return;
        }
        for (AtomicFeedback atomicFeedback : feedback.getAtomicFeedbacks()) {
            Map<String, String> metaInfo = new HashMap<>();
            metaInfo.put("percentage_of_people_affected", String.valueOf(atomicFeedback.impact));
            metaInfo.put("severity_in_percent", String.valueOf(atomicFeedback.severity));
            metaInfo.put("urgency_in_percent", String.valueOf(atomicFeedback.urgency));
            metaInfo.put("feedback_category", atomicFeedback.category.toString());
            metaInfo.put("feedback_tags_commaseparated", atomicFeedback.tags.stream().map(Tag::name).collect(Collectors.joining(", ")));
            metaInfo.put("birthyear", String.valueOf(feedback.birthYear));
            metaInfo.put("gender", feedback.gender);
            metaInfo.put("nationality", feedback.nationality);

            TextSegment segment = TextSegment.from(atomicFeedback.feedback, Metadata.from(metaInfo));
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }
    }
}
