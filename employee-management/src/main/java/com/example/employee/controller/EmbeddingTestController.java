package com.example.employee.controller;

import com.example.employee.ai.EmbeddingProvider;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/embedding")
public class EmbeddingTestController {

    private final EmbeddingProvider embeddingProvider;

    public EmbeddingTestController(
            EmbeddingProvider embeddingProvider) {
        this.embeddingProvider = embeddingProvider;
    }

    @PostMapping
    public List<Float> createEmbedding(
            @RequestBody String text) {

        return embeddingProvider.embed(text);
    }
}