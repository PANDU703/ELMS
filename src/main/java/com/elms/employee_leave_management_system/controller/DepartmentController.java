package com.elms.employee_leave_management_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elms.employee_leave_management_system.entity.Department;
import com.elms.employee_leave_management_system.repository.DepartmentRepository;

@RestController
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/api/departments")
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}