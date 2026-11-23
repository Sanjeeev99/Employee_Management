package com.example.employee.service;

import com.example.employee.ai.EmbeddingProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentRetrievalService {

    private final EmbeddingProvider embeddingProvider;
    private final JdbcTemplate ragJdbcTemplate;

    public DocumentRetrievalService(
            EmbeddingProvider embeddingProvider,
            JdbcTemplate ragJdbcTemplate) {

        this.embeddingProvider = embeddingProvider;
        this.ragJdbcTemplate = ragJdbcTemplate;
    }

    public List<Map<String, Object>> search(String question) {

        // 1. Convert question into embedding
        List<Float> embedding =
                embeddingProvider.embed(question);

        // 2. Convert vector to pgvector format
        String vector =
                embedding.toString()
                        .replace(" ", "");

        // 3. Search for the most similar chunks
        String sql =
                "SELECT " +
                "id, " +
                "content, " +
                "document_name, " +
                "page_number, " +
                "chunk_index, " +
                "1 - (embedding <=> ?::vector) AS similarity " +
                "FROM document_chunk " +
                "ORDER BY embedding <=> ?::vector " +
                "LIMIT 3";

        return ragJdbcTemplate.queryForList(
                sql,
                vector,
                vector
        );
    }
}