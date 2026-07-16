package com.elms.employee_leave_management_system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elms.employee_leave_management_system.enums.LeaveStatus;
import com.elms.employee_leave_management_system.repository.EmployeeRepository;
import com.elms.employee_leave_management_system.repository.LeaveRequestRepository;

@RestController
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public DashboardController(EmployeeRepository employeeRepository,
                               LeaveRequestRepository leaveRequestRepository) {

        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @GetMapping("/api/dashboard/stats")
    public Map<String, Long> getDashboardStats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put("employees",
                employeeRepository.count());

        stats.put("pending",
                leaveRequestRepository.countByStatus(LeaveStatus.PENDING));

        stats.put("approved",
                leaveRequestRepository.countByStatus(LeaveStatus.APPROVED));

        stats.put("rejected",
                leaveRequestRepository.countByStatus(LeaveStatus.REJECTED));

        return stats;
    }

}