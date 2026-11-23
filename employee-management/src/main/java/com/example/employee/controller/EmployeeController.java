package com.example.employee.controller;

import com.example.employee.entity.Employee;
import com.example.employee.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    // temparary start
    @GetMapping("/salary/{salary}")
    public List<Employee> getEmployeesBySalary(
            @PathVariable double salary) {

        return employeeService.getEmployeesWithSalaryGreaterThan(salary);
    }
    @GetMapping("/api/employees")
    public List<Employee> getEmployees() {

    	
    	System.out.println(employeeService.getAllEmployees()+"all employees");
    	return employeeService.getAllEmployees();
    }
    // ends

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }
    @PutMapping("/{id}/salary")
    public Employee updateSalary(@PathVariable Long id, @RequestBody Double salary) {
        return employeeService.updateSalary(id, salary);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Optional<Employee> getEmployee(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "Employee deleted successfully";
    }
    
    @GetMapping("/name/{name}")
    public Optional<Employee> getEmployeeByName(
            @PathVariable String name) {

        return employeeService.findEmployeeByName(name);
    }
}
