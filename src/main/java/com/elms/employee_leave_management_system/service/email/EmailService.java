package com.elms.employee_leave_management_system.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    public void sendLeaveApprovedEmail(String toEmail,
                                   String employeeName) {

    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom("kotagirimanoj2006@gmail.com");
    message.setTo(toEmail);

    message.setSubject("✅ Leave Approved | ELMS Pro");

    message.setText(
            "Hello " + employeeName + ",\n\n" +

            "Congratulations!\n\n" +

            "Your leave request has been APPROVED by the manager.\n\n" +

            "Enjoy your leave.\n\n" +

            "Regards,\n" +
            "ELMS Pro Team"
    );

    mailSender.send(message);
}

public void sendLeaveRejectedEmail(String toEmail,
                                   String employeeName) {

    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom("kotagirimanoj2006@gmail.com");
    message.setTo(toEmail);

    message.setSubject("❌ Leave Rejected | ELMS Pro");

    message.setText(
            "Hello " + employeeName + ",\n\n" +

            "We are sorry to inform you that your leave request has been REJECTED.\n\n" +

            "Please contact your manager for more information.\n\n" +

            "Regards,\n" +
            "ELMS Pro Team"
    );

    mailSender.send(message);
}


}