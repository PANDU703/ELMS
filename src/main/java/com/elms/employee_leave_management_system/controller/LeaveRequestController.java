
package com.elms.employee_leave_management_system.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.elms.employee_leave_management_system.entity.Employee;
import com.elms.employee_leave_management_system.entity.LeaveRequest;
import com.elms.employee_leave_management_system.entity.LeaveType;
import com.elms.employee_leave_management_system.repository.LeaveTypeRepository;
import com.elms.employee_leave_management_system.service.EmployeeService;
import com.elms.employee_leave_management_system.service.LeaveRequestService;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final EmployeeService employeeService;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${medical.ai.url:http://127.0.0.1:8000/screen}")
    private String screeningServiceUrl;

    @Value("${medical.certificate.storage-dir:./private-uploads/medical-certificates}")
    private String certificateStorageDirectory;

    public LeaveRequestController(
            LeaveRequestService leaveRequestService,
            EmployeeService employeeService,
            LeaveTypeRepository leaveTypeRepository,
            ObjectMapper objectMapper) {

        this.leaveRequestService = leaveRequestService;
        this.employeeService = employeeService;
        this.leaveTypeRepository = leaveTypeRepository;
        this.objectMapper = objectMapper;
    }

    // Existing JSON endpoint retained for compatibility
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public LeaveRequest applyLeave(
            @RequestBody LeaveRequest leaveRequest,
            Authentication authentication) {

        prepareLeaveRequest(leaveRequest, authentication);

        return leaveRequestService.applyLeave(leaveRequest);
    }

    // New multipart endpoint for certificate uploads
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LeaveRequest applyLeaveWithCertificate(
            @RequestParam("leaveRequest") String leaveRequestJson,
            @RequestParam(value = "certificate", required = false)
                    MultipartFile certificate,
            Authentication authentication) throws IOException {

        LeaveRequest leaveRequest;

        try {
            leaveRequest = objectMapper.readValue(
                    leaveRequestJson,
                    LeaveRequest.class
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Invalid leave request details."
            );
        }

        prepareLeaveRequest(leaveRequest, authentication);

        boolean sickLeave = leaveRequest.getLeaveType()
                .getLeaveName()
                .trim()
                .equalsIgnoreCase("SICK LEAVE");

        String screeningResultJson = null;

        if (sickLeave && (certificate == null || certificate.isEmpty())) {
            throw new IllegalArgumentException(
                    "A medical certificate is required for Sick Leave."
            );
        }

        if (certificate != null && !certificate.isEmpty()) {

            validateCertificate(certificate);

            Map<String, Object> screeningResult =
                    screenCertificate(certificate);

            screeningResultJson = objectMapper.writeValueAsString(
                    screeningResult
            );
        }

        return leaveRequestService.applyLeave(
                leaveRequest,
                certificate,
                screeningResultJson
        );
    }

    // Identify employee and leave type using trusted database records
    private void prepareLeaveRequest(
            LeaveRequest leaveRequest,
            Authentication authentication) {

        if (authentication == null) {
            throw new IllegalArgumentException(
                    "Please log in before applying for leave."
            );
        }

        Employee employee = employeeService.getEmployeeByEmail(
                authentication.getName()
        );

        if (leaveRequest.getLeaveType() == null
                || leaveRequest.getLeaveType().getId() == null) {

            throw new IllegalArgumentException(
                    "Please select a valid leave type."
            );
        }

        LeaveType leaveType = leaveTypeRepository.findById(
                leaveRequest.getLeaveType().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Invalid leave type.")
        );

        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
    }

    // Validate certificate image
    private void validateCertificate(MultipartFile certificate) {

        String contentType = certificate.getContentType();

        if (certificate.getSize() > 8L * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Certificate image must be 8 MB or smaller."
            );
        }

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/webp"))) {

            throw new IllegalArgumentException(
                    "Upload a JPG, PNG, or WEBP image."
            );
        }
    }

    // Send uploaded certificate to Python OCR service
    private Map<String, Object> screenCertificate(
            MultipartFile certificate) throws IOException {

        ByteArrayResource resource =
                new ByteArrayResource(certificate.getBytes()) {

            @Override
            public String getFilename() {
                return "medical-certificate";
            }
        };

        HttpHeaders imageHeaders = new HttpHeaders();

        imageHeaders.setContentType(
                MediaType.parseMediaType(certificate.getContentType())
        );

        HttpEntity<ByteArrayResource> imageEntity =
                new HttpEntity<>(resource, imageHeaders);

        MultiValueMap<String, Object> form =
                new LinkedMultiValueMap<>();

        form.add("file", imageEntity);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    screeningServiceUrl,
                    new HttpEntity<>(form, headers),
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null) {

                throw new IllegalStateException(
                        "Medical certificate screening failed."
                );
            }

            return response.getBody();

        } catch (RestClientException ex) {

            throw new IllegalStateException(
                    "Cannot contact the Python AI service. "
                    + "Please ensure it is running on port 8000."
            );
        }
    }

    @GetMapping
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestService.getAllLeaveRequests();
    }

    @GetMapping("/my")
    public List<LeaveRequest> getMyLeaveRequests(
            Authentication authentication) {

        return leaveRequestService.getMyLeaveRequests(
                authentication.getName()
        );
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

    @GetMapping("/{id}/certificate")
public ResponseEntity<?> viewCertificate(@PathVariable Long id) {

    LeaveRequest leave = leaveRequestService.getLeaveRequestById(id);

    if (leave.getCertificateStoragePath() == null
            || leave.getCertificateStoragePath().isBlank()) {
        return ResponseEntity.notFound().build();
    }

    try {
        Path storageDirectory = Paths.get(certificateStorageDirectory)
                .toAbsolutePath().normalize();

        Path certificatePath = Paths
                .get(leave.getCertificateStoragePath())
                .toAbsolutePath().normalize();

        if (!certificatePath.startsWith(storageDirectory)
                || !Files.isRegularFile(certificatePath)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = leave.getCertificateContentType();

        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)
                && !"image/webp".equals(contentType)) {
            return ResponseEntity.badRequest().build();
        }

        byte[] imageBytes = Files.readAllBytes(certificatePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"medical-certificate\""
                )
                .contentLength(imageBytes.length)
                .body(new ByteArrayResource(imageBytes));

    } catch (IOException ex) {
        return ResponseEntity.internalServerError().build();
    }
        }
}
