package com.elms.employee_leave_management_system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.elms.employee_leave_management_system.entity.LeaveRequest;
import com.elms.employee_leave_management_system.enums.LeaveStatus;
import com.elms.employee_leave_management_system.exception.ResourceNotFoundException;
import com.elms.employee_leave_management_system.repository.LeaveRequestRepository;
import com.elms.employee_leave_management_system.service.LeaveRequestService;
import com.elms.employee_leave_management_system.service.email.EmailService;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmailService emailService;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, EmailService emailService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.emailService = emailService;
    }

    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) {
        leaveRequest.setStatus(LeaveStatus.PENDING);
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    @Override
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Leave Request not found with id : " + id));
    }

    @Override
    public LeaveRequest approveLeave(Long id) {

    LeaveRequest leave = getLeaveRequestById(id);

    leave.setStatus(LeaveStatus.APPROVED);

    LeaveRequest savedLeave = leaveRequestRepository.save(leave);

    emailService.sendLeaveApprovedEmail(
            savedLeave.getEmployee().getEmail(),
            savedLeave.getEmployee().getFirstName()
    );

    return savedLeave;
    }

    @Override
    public LeaveRequest rejectLeave(Long id) {

    LeaveRequest leave = getLeaveRequestById(id);

    leave.setStatus(LeaveStatus.REJECTED);

    LeaveRequest savedLeave = leaveRequestRepository.save(leave);

    emailService.sendLeaveRejectedEmail(
            savedLeave.getEmployee().getEmail(),
            savedLeave.getEmployee().getFirstName()
    );

    return savedLeave;
}
}