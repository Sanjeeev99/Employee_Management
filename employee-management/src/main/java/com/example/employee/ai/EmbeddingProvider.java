package com.example.employee.ai;

import java.util.List;

public interface EmbeddingProvider {

    List<Float> embed(String text);
}