package com.example.employee.controller;

import com.example.employee.service.DocumentEmbeddingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag")
public class RagTestController {

    private final DocumentEmbeddingService documentEmbeddingService;

    public RagTestController(
            DocumentEmbeddingService documentEmbeddingService) {

        this.documentEmbeddingService =
                documentEmbeddingService;
    }

    @PostMapping("/chunk")
    public String saveChunk(
            @RequestBody String content) {

        documentEmbeddingService.saveChunk(
                content,
                "test-document.txt",
                1,
                1
        );

        return "Chunk saved successfully";
    }
}