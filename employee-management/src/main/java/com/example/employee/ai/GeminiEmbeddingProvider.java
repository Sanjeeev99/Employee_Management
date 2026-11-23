package com.example.employee.ai;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentResponse;
import org.springframework.stereotype.Service;
import com.google.genai.types.EmbedContentConfig;

import java.util.Collections;
import java.util.List;

@Service
public class GeminiEmbeddingProvider implements EmbeddingProvider {

    private final Client client;

    public GeminiEmbeddingProvider() {
        this.client = new Client();
    }

    @Override
    public List<Float> embed(String text) {

    	EmbedContentConfig config =
    	        EmbedContentConfig.builder()
    	                .outputDimensionality(768)
    	                .build();

    	EmbedContentResponse response =
    	        client.models.embedContent(
    	                "gemini-embedding-2",
    	                text,
    	                config
    	        );
    	
        System.out.println("embedded response:: "+response);        
        List<ContentEmbedding> embeddings =
                response.embeddings()
                        .orElse(Collections.emptyList());

        if (embeddings.isEmpty()) {
            throw new RuntimeException("Embedding was not returned");
        }
        System.out.println("final embedding response vector::"+ embeddings.get(0).values() .orElse(Collections.emptyList()));
        
        
        return embeddings.get(0)
                .values()
                .orElse(Collections.emptyList());
        
    }
}