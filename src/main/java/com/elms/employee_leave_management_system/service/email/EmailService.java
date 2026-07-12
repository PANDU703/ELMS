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
}