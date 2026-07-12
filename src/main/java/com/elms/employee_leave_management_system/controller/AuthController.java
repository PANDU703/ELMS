package com.elms.employee_leave_management_system.controller;

import org.springframework.web.bind.annotation.*;

import com.elms.employee_leave_management_system.dto.LoginRequest;
import com.elms.employee_leave_management_system.entity.Employee;
import com.elms.employee_leave_management_system.service.EmployeeService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public Employee login(@RequestBody LoginRequest request) {

        return employeeService.loginEmployee(
                request.getEmail(),
                request.getPassword()
        );
    }
}