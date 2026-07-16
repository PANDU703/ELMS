package com.elms.employee_leave_management_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.elms.employee_leave_management_system.entity.Employee;
import com.elms.employee_leave_management_system.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public Employee saveEmployee(@RequestBody Employee employee) {
       return employeeService.saveEmployee(employee);
    }



    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "Employee deleted successfully.";
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id,
                                   @RequestBody Employee employee) {
                                
        return employeeService.updateEmployee(id, employee);
    }

    @GetMapping("/me")
    public Employee getLoggedInEmployee(Authentication authentication) {

        String email = authentication.getName();

        return employeeService.getEmployeeByEmail(email);

    }
}