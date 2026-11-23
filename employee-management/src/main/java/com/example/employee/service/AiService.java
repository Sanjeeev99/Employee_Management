package com.example.employee.service;

import com.example.employee.ai.AIProvider;
import com.example.employee.ai.GroqAIProvider;

import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final AIProvider aiProvider;

    public String askStructured(String question) {
        if (aiProvider instanceof GroqAIProvider) {
            return ((GroqAIProvider) aiProvider)
                    .generateStructured(question);
        }

        throw new RuntimeException(
                "Structured output is currently supported only by Groq");
    }
    
    public AiService(AIProvider aiProvider) {
        this.aiProvider = aiProvider;
    }

    public String askAI(
            String question,
            String conversationId) {

        return aiProvider.generate(
                question,
                conversationId);
    }
}






//package com.example.employee.service;
//
//import com.example.employee.entity.Employee;
//import com.google.genai.Client;
//import com.google.genai.types.GenerateContentConfig;
//import com.google.genai.types.GenerateContentResponse;
//import com.google.genai.types.Tool;
//import org.springframework.stereotype.Service;
//
//import java.lang.reflect.Method;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class AiService {
//
//    private final Client client;
//    private final EmployeeService employeeService;
//
//    public AiService(EmployeeService employeeService) {
//        this.client = new Client();
//        this.employeeService = employeeService;
//    }
//
//    public String askAI(String question) throws Exception {
//
//        Method findEmployeeMethod =
//                AiService.class.getMethod(
//                        "findEmployeeByName",
//                        String.class);
//
//        Method salaryMethod =
//                AiService.class.getMethod(
//                        "getEmployeesWithSalaryGreaterThan",
//                        double.class);
//
//        Tool employeeTool =
//                Tool.builder()
//                        .functionWithInstance(
//                                findEmployeeMethod,
//                                this)
//                        .functionWithInstance(
//                                salaryMethod,
//                                this)
//                        .build();
//
//        GenerateContentConfig config =
//                GenerateContentConfig.builder()
//                        .tools(employeeTool)
//                        .build();
//
//        GenerateContentResponse response =
//                client.models.generateContent(
//                        "gemini-3.6-flash",
//                        question,
//                        config);
//
//        return response.text();
//    }
//
//    public String findEmployeeByName(String name) {
//
//        Optional<Employee> employee =
//                employeeService.findEmployeeByName(name);
//
//        if (employee.isPresent()) {
//
//            Employee e = employee.get();
//
//            return "Employee found. "
//                    + "Name: " + e.getName()
//                    + ", Email: " + e.getEmail()
//                    + ", Salary: " + e.getSalary();
//        }
//
//        return "No employee found with name " + name;
//    }
//
//    public String getEmployeesWithSalaryGreaterThan(double salary) {
//
//        List<Employee> employees =
//                employeeService.getEmployeesWithSalaryGreaterThan(salary);
//
//        if (employees.isEmpty()) {
//            return "No employees have salary greater than " + salary;
//        }
//
//        StringBuilder result = new StringBuilder();
//
//        for (Employee employee : employees) {
//
//            result.append("Name: ")
//                    .append(employee.getName())
//                    .append(", Salary: ")
//                    .append(employee.getSalary())
//                    .append(", Email: ")
//                    .append(employee.getEmail())
//                    .append("\n");
//        }
//
//        return result.toString();
//    }
//}
//
//
//
//
//
//
//
////package com.example.employee.service;
////
////import com.google.genai.Client;
////import com.google.genai.types.GenerateContentResponse;
////import org.springframework.stereotype.Service;
////
////@Service
////public class AiService {
////
////    private final Client client;
////
////    public AiService() {
////        this.client = new Client();
////    }
////
////    public String askAI(String question) {
////
////        GenerateContentResponse response =
////                client.models.generateContent(
////                		  "gemini-3.6-flash",
////                        question,
////                        null
////                );
////
////        return response.text();
////    }
////}