package com.elms.employee_leave_management_system.service;

import java.util.List;

import com.elms.employee_leave_management_system.entity.LeaveRequest;

public interface LeaveRequestService {

    LeaveRequest applyLeave(LeaveRequest leaveRequest);

    List<LeaveRequest> getAllLeaveRequests();

    List<LeaveRequest> getMyLeaveRequests(String email);

    LeaveRequest getLeaveRequestById(Long id);

    LeaveRequest approveLeave(Long id, String remarks);

    LeaveRequest rejectLeave(Long id, String remarks);

}