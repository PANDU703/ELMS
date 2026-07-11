package com.elms.employee_leave_management_system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.elms.employee_leave_management_system.entity.Employee;
import com.elms.employee_leave_management_system.exception.ResourceNotFoundException;
import com.elms.employee_leave_management_system.repository.EmployeeRepository;
import com.elms.employee_leave_management_system.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {

    return employeeRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Employee not found with id : " + id));

    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
    
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id : " + id));

        existingEmployee.setEmployeeCode(employee.getEmployeeCode());
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPassword(employee.getPassword());
        existingEmployee.setPhone(employee.getPhone());
        existingEmployee.setDesignation(employee.getDesignation());
        existingEmployee.setJoiningDate(employee.getJoiningDate());
        existingEmployee.setRole(employee.getRole());
        existingEmployee.setDepartment(employee.getDepartment());

        return employeeRepository.save(existingEmployee);
    }
}