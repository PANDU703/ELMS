package com.elms.employee_leave_management_system.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elms.employee_leave_management_system.entity.Employee;
import com.elms.employee_leave_management_system.exception.ResourceNotFoundException;
import com.elms.employee_leave_management_system.repository.EmployeeRepository;
import com.elms.employee_leave_management_system.service.EmployeeService;
import com.elms.employee_leave_management_system.service.email.EmailService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {

    this.employeeRepository = employeeRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
}

    @Override
    public Employee saveEmployee(Employee employee) {

    employee.setPassword(
            passwordEncoder.encode(employee.getPassword())
    );

    Employee savedEmployee = employeeRepository.save(employee);
    

    emailService.sendWelcomeEmail(

            savedEmployee.getEmail(),

            savedEmployee.getFirstName(),

            savedEmployee.getEmployeeCode()

    );

    return savedEmployee;
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

    @Override
    public Employee loginEmployee(String email, String password) {

    Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Invalid Email"));

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        return employee;
    }

    @Override
    public Employee getEmployeeByEmail(String email) {

        return employeeRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with email : " + email));

    }

}