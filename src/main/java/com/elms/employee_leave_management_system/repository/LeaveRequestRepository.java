package com.elms.employee_leave_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.elms.employee_leave_management_system.entity.LeaveRequest;
import com.elms.employee_leave_management_system.enums.LeaveStatus;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    long countByStatus(LeaveStatus status);

    List<LeaveRequest> findByEmployeeEmail(String email);

}