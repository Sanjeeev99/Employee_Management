package com.example.employee.service;

import com.example.employee.entity.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    
    

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    public Employee updateSalary(Long id, Double salary) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("employee not found"));
    employee.setSalary(salary);
    return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
    
    public List<Employee> getEmployeesWithSalaryGreaterThan(double salary) {
        return employeeRepository.findBySalaryGreaterThan(salary);
    }
    
    public Optional<Employee> findEmployeeByName(String name) {

        return employeeRepository.findByNameIgnoreCase(name);
    }
}
