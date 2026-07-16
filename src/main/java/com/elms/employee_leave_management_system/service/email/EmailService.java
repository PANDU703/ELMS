package com.elms.employee_leave_management_system.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.elms.employee_leave_management_system.entity.LeaveRequest;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        
    }

    public void sendWelcomeEmail(String toEmail,
                                 String employeeName,
                                 String employeeCode) {

        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom("kotagirimanoj2006@gmail.com");

        message.setTo(toEmail);

        message.setSubject("🎉 Welcome to ELMS Pro");

        message.setText(
                "Hello " + employeeName + ",\n\n" +

                "Welcome to ELMS Pro!\n\n" +

                "Your account has been created successfully.\n\n" +

                "Employee Code : " + employeeCode + "\n" +
                "Email : " + toEmail + "\n\n" +

                "We are excited to have you onboard.\n\n" +

                "Regards,\n" +
                "ELMS Pro Team"
        );

        mailSender.send(message);
    }

public void sendLeaveApprovedEmail(LeaveRequest leaveRequest) {

    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom("kotagirimanoj2006@gmail.com");

    message.setTo(leaveRequest.getEmployee().getEmail());

    message.setSubject("✅ ELMS Pro | Leave Request Approved");

    message.setText(

            "Dear " + leaveRequest.getEmployee().getFirstName() + ",\n\n" +

            "Greetings from ELMS Pro.\n\n" +

            "We are pleased to inform you that your leave request has been APPROVED.\n\n" +

            "------------------------------------------\n" +

            "Employee Code : " + leaveRequest.getEmployee().getEmployeeCode() + "\n" +

            "Leave Type : " + leaveRequest.getLeaveType().getLeaveName() + "\n" +

            "Start Date : " + leaveRequest.getStartDate() + "\n" +

            "End Date : " + leaveRequest.getEndDate() + "\n" +

            "Total Days : " + leaveRequest.getTotalDays() + "\n\n" +

            "Manager Remarks :\n" +

            leaveRequest.getManagerRemarks() + "\n\n" +

            "Enjoy your leave!\n\n" +

            "Regards,\n" +

            "ELMS Pro Team"

    );

    mailSender.send(message);
}

    public void sendLeaveRejectedEmail(LeaveRequest leaveRequest) {

    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom("kotagirimanoj2006@gmail.com");

    message.setTo(leaveRequest.getEmployee().getEmail());

    message.setSubject("❌ ELMS Pro | Leave Request Rejected");

    message.setText(

            "Dear " + leaveRequest.getEmployee().getFirstName() + ",\n\n" +

            "Greetings from ELMS Pro.\n\n" +

            "Your leave request has been REJECTED.\n\n" +

            "------------------------------------------\n" +

            "Employee Code : " + leaveRequest.getEmployee().getEmployeeCode() + "\n" +

            "Leave Type : " + leaveRequest.getLeaveType().getLeaveName() + "\n" +

            "Start Date : " + leaveRequest.getStartDate() + "\n" +

            "End Date : " + leaveRequest.getEndDate() + "\n" +

            "Total Days : " + leaveRequest.getTotalDays() + "\n\n" +

            "Manager Remarks :\n" +

            leaveRequest.getManagerRemarks() + "\n\n" +

            "If you have any questions, please contact your manager.\n\n" +

            "Regards,\n" +

            "ELMS Pro Team"

    );

    mailSender.send(message);
    }
}