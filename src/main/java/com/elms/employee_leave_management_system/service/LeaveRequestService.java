
package com.elms.employee_leave_management_system.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.elms.employee_leave_management_system.entity.LeaveRequest;

public interface LeaveRequestService {

    LeaveRequest applyLeave(LeaveRequest leaveRequest);

    LeaveRequest applyLeave(
            LeaveRequest leaveRequest,
            MultipartFile certificate,
            String screeningResultJson
    ) throws IOException;

    List<LeaveRequest> getAllLeaveRequests();

    List<LeaveRequest> getMyLeaveRequests(String email);

    LeaveRequest getLeaveRequestById(Long id);

    LeaveRequest approveLeave(Long id, String remarks);

    LeaveRequest rejectLeave(Long id, String remarks);
}
