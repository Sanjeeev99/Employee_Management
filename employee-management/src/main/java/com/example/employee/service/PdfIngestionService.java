package com.example.employee.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfIngestionService {

    private final DocumentEmbeddingService documentEmbeddingService;
    private final JdbcTemplate ragJdbcTemplate;

    public PdfIngestionService(
            DocumentEmbeddingService documentEmbeddingService,
            @Qualifier("ragJdbcTemplate") JdbcTemplate ragJdbcTemplate) {
        this.documentEmbeddingService = documentEmbeddingService;
        this.ragJdbcTemplate = ragJdbcTemplate;
    }

    public int ingestCompanyPolicyPdf(MultipartFile file, String policyCategory) throws Exception {
        String documentName = file.getOriginalFilename();

        // Remove older chunks if this exact company policy PDF is being re-uploaded
        ragJdbcTemplate.update("DELETE FROM document_chunk WHERE document_name = ?", documentName);

        int totalChunksSaved = 0;

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = document.getNumberOfPages();

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document).trim();

                if (pageText.isEmpty()) {
                    continue;
                }

                List<String> chunks = chunkWithOverlap(pageText, 500, 50);

                for (int i = 0; i < chunks.size(); i++) {
                    documentEmbeddingService.saveChunk(
                            chunks.get(i),
                            documentName,
                            page,
                            i           
                    );
                    totalChunksSaved++;
                }
            }
        }
        return totalChunksSaved;
    }

    private List<String> chunkWithOverlap(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));

            if (end == text.length()) {
                break;
            }
            start += (chunkSize - overlap);
        }
        return chunks;
    }
}