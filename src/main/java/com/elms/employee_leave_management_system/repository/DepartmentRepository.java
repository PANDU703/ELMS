package com.elms.employee_leave_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elms.employee_leave_management_system.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}