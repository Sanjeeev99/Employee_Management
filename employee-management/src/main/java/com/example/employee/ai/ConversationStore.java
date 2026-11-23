package com.example.employee.ai;

import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class ConversationStore {

    private final Map<String, List<Map<String, Object>>> conversations =
            new HashMap<>();

    public List<Map<String, Object>> getConversation(
            String conversationId) {

        return conversations.computeIfAbsent(
                conversationId,
                id -> new ArrayList<>()
        );
    }
}