package com.elms.employee_leave_management_system.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.elms.employee_leave_management_system.entity.LeaveRequest;
import com.elms.employee_leave_management_system.service.LeaveRequestService;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    public LeaveRequest applyLeave(@RequestBody LeaveRequest leaveRequest) {
        return leaveRequestService.applyLeave(leaveRequest);
    }

    @GetMapping
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestService.getAllLeaveRequests();
    }

    @GetMapping("/my")
    public List<LeaveRequest> getMyLeaveRequests(Authentication authentication) {
        return leaveRequestService.getMyLeaveRequests(authentication.getName());
    }

    @PutMapping("/{id}/approve")
    public LeaveRequest approveLeave(
            @PathVariable Long id,
            @RequestBody LeaveRequest leaveRequest) {

        return leaveRequestService.approveLeave(
                id,
                leaveRequest.getManagerRemarks()
        );
    }

    @PutMapping("/{id}/reject")
    public LeaveRequest rejectLeave(
            @PathVariable Long id,
            @RequestBody LeaveRequest leaveRequest) {

        return leaveRequestService.rejectLeave(
                id,
                leaveRequest.getManagerRemarks()
        );
    }
}