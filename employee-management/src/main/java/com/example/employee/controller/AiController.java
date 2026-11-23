package com.example.employee.controller;

import com.example.employee.service.AiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }
    
    @PostMapping("/structured")
    public String structured(@RequestBody String question) {
        return aiService.askStructured(question);
    }

 //   @PostMapping("/chat")
    @PostMapping("/chat/{conversationId}")
    public String chat(
            @PathVariable String conversationId,
            @RequestBody String question) {

        return aiService.askAI(
                question,
                conversationId);
    }
}




//package com.example.employee.controller;
//
//import com.example.employee.service.AiService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/ai")
//public class AiController {
//
//    private final AiService aiService;
//
//    public AiController(AiService aiService) {
//        this.aiService = aiService;
//    }
//
//    @PostMapping("/chat")
//    public String chat(@RequestBody String question) {
//
//        return aiService.askAI(question);
//    }
//}