package com.example.employee.service;

import com.example.employee.ai.AIProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private final DocumentRetrievalService retrievalService;
    private final AIProvider aiProvider;

    public RagService(
            DocumentRetrievalService retrievalService,
            AIProvider aiProvider) {

        this.retrievalService = retrievalService;
        this.aiProvider = aiProvider;
    }

    public String ask(String question) {

        // 1. Retrieve relevant chunks
        List<Map<String, Object>> chunks =
                retrievalService.search(question);

        // 2. Build context
        StringBuilder context =
                new StringBuilder();
        System.out.println("chunks we get from vectorPG::"+chunks);   
        for (Map<String, Object> chunk : chunks) {

            context.append("Document: ")
                    .append(chunk.get("document_name"))
                    .append("\n");

            context.append("Page: ")
                    .append(chunk.get("page_number"))
                    .append("\n");

            context.append("Content: ")
                    .append(chunk.get("content"))
                    .append("\n\n");
        }

        
        System.out.println("context we get from chunks::"+context);     
        // 3. Build prompt for LLM
        String prompt =
                "Answer the user's question using ONLY the " +
                "provided context.\n\n" +

                "If the answer is not present in the context, " +
                "say that the information was not found in the " +
                "provided documents.\n\n" +

                "Do not invent information.\n\n" +

                "CONTEXT:\n" +
                context +

                "USER QUESTION:\n" +
                question;

        // 4. Send context + question to Groq
        return aiProvider.generate(
                prompt,
                "rag"
        );
    }
}