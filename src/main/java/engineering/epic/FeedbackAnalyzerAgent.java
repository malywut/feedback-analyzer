package engineering.epic;

import dev.langchain4j.service.SystemMessage;

public interface FeedbackAnalyzerAgent {
    // Method to generate atomic feedback components
    @SystemMessage("""
            The user gives feedback in the scope of a coding lab where participants learn to interact
             with LLMs from Java using framework LangChain4j. The scope of this feedback is a 2h coding lab 
             with 60 java developers insterested in AI and LLM-powered applications. 
             Analyze and categorize the user feedback with respect to this scope.
   """)
    public AtomicFeedback generateAtomicFeedbackComponents(String atomicFeedback);
}

