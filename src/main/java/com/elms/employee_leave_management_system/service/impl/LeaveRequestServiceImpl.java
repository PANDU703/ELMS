
package com.elms.employee_leave_management_system.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${medical.certificate.storage-dir:./private-uploads/medical-certificates}")
    private String certificateStorageDirectory;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmailService emailService) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.emailService = emailService;
    }

    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) {

        if (isSickLeave(leaveRequest)) {
            throw new IllegalArgumentException(
                    "A medical certificate is required for Sick Leave."
            );
        }

        leaveRequest.setStatus(LeaveStatus.PENDING);

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequest applyLeave(
            LeaveRequest leaveRequest,
            MultipartFile certificate,
            String screeningResultJson) throws IOException {

        boolean sickLeave = isSickLeave(leaveRequest);

        if (sickLeave) {
            if (certificate == null || certificate.isEmpty()) {
                throw new IllegalArgumentException(
                        "Please upload a medical certificate for Sick Leave."
                );
            }

            if (screeningResultJson == null ||
                    screeningResultJson.isBlank()) {
                throw new IllegalArgumentException(
                        "Medical certificate screening result is missing."
                );
            }
        }

        Path savedFile = null;

        try {
            if (certificate != null && !certificate.isEmpty()) {

                Path directory = Paths.get(certificateStorageDirectory)
                        .toAbsolutePath()
                        .normalize();

                Files.createDirectories(directory);

                String extension = getExtension(
                        certificate.getContentType()
                );

                String storedFileName = UUID.randomUUID() + extension;

                savedFile = directory.resolve(storedFileName);

                Files.copy(certificate.getInputStream(), savedFile);

                String originalName = certificate.getOriginalFilename();

                leaveRequest.setCertificateFileName(
                        originalName == null
                                ? "medical-certificate"
                                : originalName.replaceAll(
                                        "[\\\\/\\r\\n\\t]", "_"
                                )
                );

                leaveRequest.setCertificateContentType(
                        certificate.getContentType()
                );

                leaveRequest.setCertificateStoragePath(
                        savedFile.toString()
                );

                leaveRequest.setScreeningResultJson(
                        screeningResultJson
                );
            }

            leaveRequest.setStatus(LeaveStatus.PENDING);

            return leaveRequestRepository.save(leaveRequest);

        } catch (IOException | RuntimeException ex) {

            if (savedFile != null) {
                try {
                    Files.deleteIfExists(savedFile);
                } catch (IOException cleanupException) {
                    ex.addSuppressed(cleanupException);
                }
            }

            throw ex;
        }
    }

    private boolean isSickLeave(LeaveRequest leaveRequest) {

        return leaveRequest.getLeaveType() != null
                && leaveRequest.getLeaveType().getLeaveName() != null
                && leaveRequest.getLeaveType().getLeaveName()
                        .trim().equalsIgnoreCase("SICK LEAVE");
    }

    private String getExtension(String contentType) {

        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            return ".jpg";
        }

        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }

        if ("image/webp".equalsIgnoreCase(contentType)) {
            return ".webp";
        }

        throw new IllegalArgumentException(
                "Unsupported medical certificate image format."
        );
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    @Override
    public List<LeaveRequest> getMyLeaveRequests(String email) {
        return leaveRequestRepository.findByEmployeeEmail(email);
    }

    @Override
    public LeaveRequest getLeaveRequestById(Long id) {

        return leaveRequestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave Request not found with id : " + id
                        ));
    }

    @Override
    public LeaveRequest approveLeave(Long id, String remarks) {

        LeaveRequest leave = getLeaveRequestById(id);

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setManagerRemarks(remarks);

        LeaveRequest savedLeave = leaveRequestRepository.save(leave);

        emailService.sendLeaveApprovedEmail(savedLeave);

        return savedLeave;
    }

    @Override
    public LeaveRequest rejectLeave(Long id, String remarks) {

        LeaveRequest leave = getLeaveRequestById(id);

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerRemarks(remarks);

        LeaveRequest savedLeave = leaveRequestRepository.save(leave);

        emailService.sendLeaveRejectedEmail(savedLeave);

        return savedLeave;
    }
}
