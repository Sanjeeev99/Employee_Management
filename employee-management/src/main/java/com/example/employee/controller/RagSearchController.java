package com.example.employee.controller;

import com.example.employee.service.DocumentRetrievalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RagSearchController {

    private final DocumentRetrievalService retrievalService;

    public RagSearchController(
            DocumentRetrievalService retrievalService) {

        this.retrievalService = retrievalService;
    }

    @PostMapping("/search")
    public List<Map<String, Object>> search(
            @RequestBody String question) {

        return retrievalService.search(question);
    }
}