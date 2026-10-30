
package com.elms.employee_leave_management_system.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/medical-certificates")
public class MedicalCertificateController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${medical.ai.url:http://127.0.0.1:8000/screen}")
    private String screeningServiceUrl;

    @PostMapping(value = "/screen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> screenCertificate(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a certificate image."));
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Upload a JPG, PNG, or WEBP image."));
        }

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                String name = file.getOriginalFilename();
                return name == null ? "certificate.png"
                        : name.replaceAll("[^A-Za-z0-9._-]", "_");
            }
        };

        HttpHeaders imageHeaders = new HttpHeaders();
        imageHeaders.setContentType(MediaType.parseMediaType(contentType));

        HttpEntity<ByteArrayResource> imageEntity =
                new HttpEntity<>(resource, imageHeaders);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", imageEntity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    screeningServiceUrl,
                    new HttpEntity<>(form, headers),
                    Map.class);

            return ResponseEntity.status(response.getStatusCode())
                    .body(response.getBody());

        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "error", "Cannot contact the Python AI service. "
                                    + "Check that it is running on port 8000."));
        }
    }
}
