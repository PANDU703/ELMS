package com.elms.employee_leave_management_system.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phone;

    private String designation;

    private Long departmentId;
}