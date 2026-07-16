package com.elms.employee_leave_management_system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.elms.employee_leave_management_system.entity.LeaveType;
import com.elms.employee_leave_management_system.repository.LeaveTypeRepository;
import com.elms.employee_leave_management_system.service.LeaveTypeService;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeServiceImpl(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Override
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

}