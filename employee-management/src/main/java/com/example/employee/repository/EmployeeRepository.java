//package com.example.employee.repository;
//
//import com.example.employee.entity.Employee;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//}


// ----AI IMPLEMENTATION------

package com.example.employee.repository;

import com.example.employee.entity.Employee;
//import com.google.common.base.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.*;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findBySalaryGreaterThan(double salary);
    
    Optional<Employee> findByNameIgnoreCase(String name);
}