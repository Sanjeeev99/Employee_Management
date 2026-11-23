package com.example.employee.ai;
import org.springframework.beans.factory.annotation.Value;

import com.example.employee.dto.EmployeeAIResponse;
import com.example.employee.entity.Employee;
import com.example.employee.service.DocumentRetrievalService;
import com.example.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.api.client.util.Value;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.*;

@Service
public class GroqAIProvider implements AIProvider {
	private final DocumentRetrievalService documentRetrievalService;
	private final ConversationStore conversationStore;
    private final RestTemplate restTemplate;
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    private static final String URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private static final String MODEL =
            "openai/gpt-oss-20b";

    public GroqAIProvider(EmployeeService employeeService,  ConversationStore conversationStore, DocumentRetrievalService documentRetrievalService, @Value("${groq.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.employeeService = employeeService;
        this.objectMapper = new ObjectMapper();
        this.conversationStore =  conversationStore;
        this.documentRetrievalService= documentRetrievalService;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    @Override
    public String generate(String question, String conversationId) {

     //   String apiKey = System.getenv("GROQ_API_KEY");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

   //     List<Map<String, Object>> messages = new ArrayList<>();
        List<Map<String, Object>> messages =
                conversationStore.getConversation(conversationId);
        
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put(
                "content",
                "\"You are an Employee Management and Company Policy Assistant. \" +\r\n"
                + "\"Use the provided employee tools for employee database records, and use searchCompanyDocuments for company policies, "
                + "leave rules, and handbooks. \" +\r\n"
                + "\"Never invent information. If the tools do not provide the answer, say that the information was not found.\" ;"
        );

        messages.add(systemMessage);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", question);

        messages.add(userMessage);

        Map<String, Object> request = new HashMap<>();
        request.put("model", MODEL);
        request.put("messages", messages);
        request.put("tools", createTools());
        request.put("tool_choice", "auto");

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(request, headers);

        Map<String, Object> response =
                restTemplate.postForObject(
                        URL,
                        entity,
                        Map.class);
        
        System.out.println("final messages::"+messages);

        return processResponse(response, messages, headers);
    }

    // ----------------------------------------------------
    // Process Groq response
    // ----------------------------------------------------

    private String processResponse(
            Map<String, Object> response,
            List<Map<String, Object>> messages,
            HttpHeaders headers) {
    	System.out.println("first response::"+response);
//    	List<Map<String, Object>> choices =
//                (List<Map<String, Object>>) response.get("choices");
//
//        Map<String, Object> choice = choices.get(0);
//
//        Map<String, Object> message =
//                (Map<String, Object>) choice.get("message");
//
//        System.out.println("respose from message::"+message);
//        // Did AI ask us to call a function?
//        List<Map<String, Object>> toolCalls =
//                (List<Map<String, Object>>) message.get("tool_calls");
//        System.out.println("first toolcall::"+toolCalls);
//
//        // Normal AI response
//        if (toolCalls == null || toolCalls.isEmpty()) {
//            return (String) message.get("content");
//        }
//
//        // Add AI's tool-call message to conversation
//        messages.add(message);
//        System.out.println("firstMessage::"+messages);
    	
    	
    	// take upto 5 function related question code
    	
    	Map<String, Object> currentResponse = response;
    	System.out.println("currentResponse:: "+currentResponse);
    	int maxIterations =5;
    	
    	for(int step=0;step<maxIterations;step++) {
    		List<Map<String, Object>> choices= (List<Map<String,Object>>) currentResponse.get("choices");
    	Map<String, Object> choice = choices.get(0);
    	Map<String, Object> message = (Map<String,Object>) choice.get("message");
    	
    	List<Map<String,Object>> toolCalls = (List<Map<String,Object>>) message.get("tool_calls");
    	
    	//if no more tool calls are reqyuested return the final answer
    	System.out.println(step+" toolCalls:: "+toolCalls);
    	if(toolCalls == null || toolCalls.isEmpty()) {
    		String finalContent = (String) message.get("content");
    		messages.add(message);
    		System.out.println(step+"finalContent:: "+finalContent);
    		return finalContent;
    	}
    	
    	//reocrd assistant's tool_calls turn in conversation history
    	messages.add(message);
    	
    	
    	

        // Execute every requested tool
        for (Map<String, Object> toolCall : toolCalls) {

            String toolCallId =
                    (String) toolCall.get("id");

            Map<String, Object> function =
                    (Map<String, Object>) toolCall.get("function");

            String functionName =
                    (String) function.get("name");

            String arguments =
                    (String) function.get("arguments");

            String toolResult =
                    executeTool(functionName, arguments);

            Map<String, Object> toolMessage =
                    new HashMap<>();

            toolMessage.put("role", "tool");
            toolMessage.put("tool_call_id", toolCallId);
            toolMessage.put("content", toolResult);

            messages.add(toolMessage);
        }

        // Send database result back to Groq
        Map<String, Object> secondRequest =
                new HashMap<>();

        secondRequest.put("model", MODEL);
        secondRequest.put("messages", messages);
        secondRequest.put("tools", createTools());
        secondRequest.put("tool_choice","auto");

        HttpEntity<Map<String, Object>> secondEntity =
                new HttpEntity<>(secondRequest, headers);

        Map<String, Object> secondResponse =
                restTemplate.postForObject(
                        URL,
                        secondEntity,
                        Map.class);
        
        System.out.println("second Response ::"+ secondResponse);

        List<Map<String, Object>> secondChoices =
                (List<Map<String, Object>>)
                        secondResponse.get("choices");

        Map<String, Object> secondChoice =
                secondChoices.get(0);

        Map<String, Object> secondMessage =
                (Map<String, Object>)
                        secondChoice.get("message");
        
        System.out.println("second content ::"+ (String) secondMessage.get("content"));
        
        currentResponse=secondResponse;

    	}
    	return "Maximum tool execution iterations reached.";
    	
    }

    // ----------------------------------------------------
    // Execute employee functions
    // ----------------------------------------------------

    private String executeTool(
            String functionName,
            String arguments) {
    	

        try {
        	
        	System.out.println("function name and args::"+functionName+" args::"+arguments);

            Map<String, Object> args =
                    objectMapper.readValue(
                            arguments,
                            Map.class);

            System.out.println("function name only::"+functionName+" args::"+args);
            if ("findEmployeeByName".equals(functionName)) {

                String name =
                        (String) args.get("name");

                Optional<Employee> employee =
                        employeeService
                                .findEmployeeByName(name);

                if (employee.isPresent()) {

                    Employee e = employee.get();

                    return "Employee found. "
                            + "Name: " + e.getName()
                            + ", Email: " + e.getEmail()
                            + ", Salary: " + e.getSalary();
                }

                return "No employee found with name " + name;
            }
            
            //DOCUMENT RETRIVAL FUNCTION
            
         // DOCUMENT RETRIEVAL FUNCTION
            if ("searchCompanyDocuments".equals(functionName)) {

                String query = (String) args.get("query");

                List<Map<String, Object>> chunks =
                        documentRetrievalService.search(query);
                if (chunks.isEmpty()) {
                    return "No relevant company documents found.";
                }

                StringBuilder result = new StringBuilder();

                for (Map<String, Object> chunk : chunks) {
                    result.append("Document: ")
                            .append(chunk.get("document_name"))
                            .append(" (Page ")
                            .append(chunk.get("page_number"))
                            .append("): ")
                            .append(chunk.get("content"))
                            .append("\n");
                }

                return result.toString();
            }

            if ("getEmployeesWithSalaryGreaterThan"
                    .equals(functionName)) {

                Number salary =
                        (Number) args.get("salary");

                List<Employee> employees =
                        employeeService
                                .getEmployeesWithSalaryGreaterThan(
                                        salary.doubleValue());

                if (employees.isEmpty()) {
                    return "No employees have salary greater than "
                            + salary;
                }

                StringBuilder result =
                        new StringBuilder();

                for (Employee e : employees) {

                    result.append("Name: ")
                            .append(e.getName())
                            .append(", Email: ")
                            .append(e.getEmail())
                            .append(", Salary: ")
                            .append(e.getSalary())
                            .append("\n");
                }

                return result.toString();
            }

            return "Unknown function: " + functionName;

        } catch (Exception e) {

            return "Error executing employee function: "
                    + e.getMessage();
        }
        
    }

    // ----------------------------------------------------
    // Tell Groq what functions are available
    // ----------------------------------------------------

    private List<Map<String, Object>> createTools() {
    	
    	
    	List<Map<String, Object>> tools =
                new ArrayList<>();
    	
    	//DOCUMENT RETRIVAL FUNCTION ADDED
    	
    	Map<String, Object> docTool = new HashMap<>();
    	docTool.put("type", "function");

    	Map<String, Object> docFunction = new HashMap<>();
    	docFunction.put("name", "searchCompanyDocuments");
    	docFunction.put("description", "Search company policies, HR handbooks, leave rules, and internal documents.");

    	Map<String, Object> docParams = new HashMap<>();
    	docParams.put("type", "object");

    	Map<String, Object> queryProp = new HashMap<>();
    	queryProp.put("type", "string");
    	queryProp.put("description", "The policy or document topic to search for");

    	Map<String, Object> docProps = new HashMap<>();
    	docProps.put("query", queryProp);

    	docParams.put("properties", docProps);
    	docParams.put("required", Collections.singletonList("query"));
    	docFunction.put("parameters", docParams);
    	docTool.put("function", docFunction);

    	tools.add(docTool);
    	System.out.println("docTools:: "+docTool);
        

        // findEmployeeByName
        Map<String, Object> findEmployee =
                new HashMap<>();

        findEmployee.put("type", "function");

        Map<String, Object> findFunction =
                new HashMap<>();

        findFunction.put(
                "name",
                "findEmployeeByName");

        findFunction.put(
                "description",
                "Find an employee by their name and return their details.");

        Map<String, Object> findParameters =
                new HashMap<>();

        findParameters.put("type", "object");

        Map<String, Object> nameProperty =
                new HashMap<>();

        nameProperty.put(
                "type",
                "string");

        nameProperty.put(
                "description",
                "Employee name");

        Map<String, Object> properties =
                new HashMap<>();

        properties.put(
                "name",
                nameProperty);

        findParameters.put(
                "properties",
                properties);

        findParameters.put(
                "required",
                Collections.singletonList("name"));

        findFunction.put(
                "parameters",
                findParameters);

        findEmployee.put(
                "function",
                findFunction);

        tools.add(findEmployee);

        // getEmployeesWithSalaryGreaterThan
        Map<String, Object> salaryTool =
                new HashMap<>();

        salaryTool.put("type", "function");

        Map<String, Object> salaryFunction =
                new HashMap<>();

        salaryFunction.put(
                "name",
                "getEmployeesWithSalaryGreaterThan");

        salaryFunction.put(
                "description",
                "Find employees whose salary is greater than the given amount.");

        Map<String, Object> salaryParameters =
                new HashMap<>();

        salaryParameters.put(
                "type",
                "object");

        Map<String, Object> salaryProperty =
                new HashMap<>();

        salaryProperty.put(
                "type",
                "number");

        salaryProperty.put(
                "description",
                "Minimum salary");

        Map<String, Object> salaryProperties =
                new HashMap<>();

        salaryProperties.put(
                "salary",
                salaryProperty);

        salaryParameters.put(
                "properties",
                salaryProperties);

        salaryParameters.put(
                "required",
                Collections.singletonList("salary"));

        salaryFunction.put(
                "parameters",
                salaryParameters);

        salaryTool.put(
                "function",
                salaryFunction);

        tools.add(salaryTool);
        
        System.out.println("available tools :: "+ tools);

        return tools;
    }
    
    public String generateStructured(String question) {

   //     String apiKey = System.getenv("GROQ_API_KEY");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", question);
        messages.add(userMessage);

        Map<String, Object> request = new HashMap<>();

        request.put("model", MODEL);
        request.put("messages", messages);

        request.put(
            "response_format",
            createEmployeeResponseFormat()
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(request, headers);

        Map<String, Object> response =
                restTemplate.postForObject(
                        URL,
                        entity,
                        Map.class);

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>)
                        response.get("choices");

        Map<String, Object> choice =
                choices.get(0);

        Map<String, Object> message =
                (Map<String, Object>)
                        choice.get("message");

        String content = (String) message.get("content");
        
        System.out.println("content in jason structured result: "+content);

        try {
            EmployeeAIResponse employee =
                    objectMapper.readValue(
                            content,
                            EmployeeAIResponse.class);

//            System.out.println("Employee name: " + employee.getName());
//            System.out.println("Employee email: " + employee.getEmail());
//            System.out.println("Employee salary: " + employee.getSalary());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("content in dto format: "+content);
        return content;
    }
    
    private Map<String, Object> createEmployeeResponseFormat() {

        Map<String, Object> responseFormat =
                new HashMap<>();

        responseFormat.put("type", "json_schema");

        Map<String, Object> jsonSchema =
                new HashMap<>();

        jsonSchema.put("name", "employee_response");
        jsonSchema.put("strict", true);

        Map<String, Object> schema =
                new HashMap<>();

        schema.put("type", "object");

        Map<String, Object> properties =
                new HashMap<>();

        Map<String, Object> name =
                new HashMap<>();
        name.put("type", "string");

        Map<String, Object> email =
                new HashMap<>();
        email.put("type", "string");

        Map<String, Object> salary =
                new HashMap<>();
        salary.put("type", "number");

        properties.put("name", name);
        properties.put("email", email);
        properties.put("salary", salary);

        schema.put("properties", properties);

        schema.put(
                "required",
                Arrays.asList("name", "email", "salary")
        );

        schema.put("additionalProperties", false);

        jsonSchema.put("schema", schema);

        responseFormat.put("json_schema", jsonSchema);

        System.out.println("responseFormat data:: "+responseFormat);   
        return responseFormat;
    }
}