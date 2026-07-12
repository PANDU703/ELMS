package com.elms.employee_leave_management_system.service;

import java.util.List;

import com.elms.employee_leave_management_system.entity.Employee;

public interface EmployeeService {

    Employee saveEmployee(Employee employee);

    List<Employee> getAllEmployees();

    Employee getEmployeeById(Long id);

    Employee updateEmployee(Long id, Employee employee);

    void deleteEmployee(Long id);

    Employee loginEmployee(String email, String password);

}