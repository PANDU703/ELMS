package com.elms.employee_leave_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elms.employee_leave_management_system.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}