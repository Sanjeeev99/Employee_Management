package com.example.employee.controller;

import com.example.employee.service.DocumentEmbeddingService;
import com.example.employee.service.PdfIngestionService;
import com.example.employee.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;
    private final DocumentEmbeddingService documentEmbeddingService;
    private final PdfIngestionService pdfIngestionService;

    public RagController(
            RagService ragService,
            DocumentEmbeddingService documentEmbeddingService,
            PdfIngestionService pdfIngestionService) {
        this.ragService = ragService;
        this.documentEmbeddingService = documentEmbeddingService;
        this.pdfIngestionService = pdfIngestionService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        return ragService.ask(question);
    }

    @PostMapping("/policies/upload")
    public  ResponseEntity<Map<String, Object>> uploadCompanyPolicy(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "COMPANY_POLICY") String category) {

        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty() || file.getOriginalFilename() == null
                || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            response.put("error", "Please upload a valid company policy PDF file.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            int chunksCount = pdfIngestionService.ingestCompanyPolicyPdf(file, category);
            response.put("message", "Company policy PDF uploaded and indexed successfully.");
            response.put("policyFile", file.getOriginalFilename());
            response.put("category", category);
            response.put("chunksStored", chunksCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to process policy PDF: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}