package com.example.employee.service;

import com.example.employee.ai.EmbeddingProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentEmbeddingService {

    private final EmbeddingProvider embeddingProvider;
    private final JdbcTemplate ragJdbcTemplate;

    public DocumentEmbeddingService(
            EmbeddingProvider embeddingProvider,
            JdbcTemplate ragJdbcTemplate) {

        this.embeddingProvider = embeddingProvider;
        this.ragJdbcTemplate = ragJdbcTemplate;
    }

    public void saveChunk(
            String content,
            String documentName,
            Integer pageNumber,
            Integer chunkIndex) {

        List<Float> embedding =
                embeddingProvider.embed(content);

        String vector =
                embedding.toString()
                        .replace(" ", "");

        String sql =
                "INSERT INTO document_chunk " +
                "(content, embedding, document_name, page_number, chunk_index) " +
                "VALUES (?, ?::vector, ?, ?, ?)";

        ragJdbcTemplate.update(
                sql,
                content,
                vector,
                documentName,
                pageNumber,
                chunkIndex
        );
    }
}