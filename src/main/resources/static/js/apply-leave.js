document.addEventListener("DOMContentLoaded", () => {    
    loadLeaveTypes();
    const leaveForm = document.getElementById("leaveForm");
    const leaveTypeDropdown = document.getElementById("leaveTypeId");
    const certificateSection =
        document.getElementById("certificateSection");
    const certificateInput =
        document.getElementById("certificateFile");
    const submitButton =
        document.getElementById("submitButton");
    const message =
        document.getElementById("message");
    // Show certificate field only for Sick Leave
    leaveTypeDropdown.addEventListener("change", () => {
        const selectedOption =
            leaveTypeDropdown.options[leaveTypeDropdown.selectedIndex];
        const isSickLeave =
            selectedOption &&
            selectedOption.text.trim().toUpperCase() === "SICK LEAVE";
        certificateSection.classList.toggle(
            "d-none",
            !isSickLeave
        );
        certificateInput.required = isSickLeave;
        if (!isSickLeave) {
            certificateInput.value = "";
        }
    });
    leaveForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        message.className = "mt-3";
        message.textContent = "";
        const leaveTypeId = leaveTypeDropdown.value;
        const startDate = document.getElementById("startDate").value;
        const endDate = document.getElementById("endDate").value;
        const reason = document.getElementById("reason").value;
        const selectedOption =
            leaveTypeDropdown.options[leaveTypeDropdown.selectedIndex];
        const isSickLeave =
            selectedOption &&
            selectedOption.text.trim().toUpperCase() === "SICK LEAVE";
        if (!leaveTypeId || !startDate || !endDate || !reason.trim()) {
            showMessage("Please complete all required fields.", "danger");
            return;
        }
        if (new Date(endDate) < new Date(startDate)) {
            showMessage(
                "End date cannot be earlier than start date.",
                "danger"
            );
            return;
        }
        const start = new Date(startDate + "T00:00:00");
        const end = new Date(endDate + "T00:00:00");
        const totalDays =
            Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1;
        const certificate = certificateInput.files[0];
        if (isSickLeave && !certificate) {
            showMessage(
                "Please upload your medical certificate for Sick Leave.",
                "danger"
            );
            return;
        }
        if (certificate) {

            if (certificate.size > 8 * 1024 * 1024) {
                showMessage(
                    "Certificate must be 8 MB or smaller.",
                    "danger"
                );
                return;
            }
            const allowedTypes = [
                "image/jpeg",
                "image/png",
                "image/webp"
            ];
            if (!allowedTypes.includes(certificate.type)) {
                showMessage(
                    "Please upload a JPG, PNG or WEBP image.",
                    "danger"
                );
                return;
            }
        }
        const leaveRequest = {
            leaveType: {
                id: Number(leaveTypeId)
            },
            startDate: startDate,
            endDate: endDate,
            totalDays: totalDays,
            reason: reason.trim()
        };
        const formData = new FormData();
        formData.append(
            "leaveRequest",
            JSON.stringify(leaveRequest)
        );
        if (certificate) {
            formData.append("certificate", certificate);
        }
        submitButton.disabled = true;
        submitButton.textContent = "Submitting...";
        try {
            showMessage(
                isSickLeave
                    ? "Screening certificate and submitting leave request. Please wait..."
                    : "Submitting leave request. Please wait...",
                "info"
            );
            const response = await fetch("/api/leaves", {
                method: "POST",
                body: formData
            });
            const responseText = await response.text();
            if (!response.ok) {
                let errorMessage = "Failed to apply for leave.";
                try {
                    const errorData = JSON.parse(responseText);
                    errorMessage =
                        errorData.message ||
                        errorData.error ||
                        errorData.detail ||
                        errorMessage;
                } catch (e) {
                    if (responseText) {
                        errorMessage = responseText;
                    }
                }
                throw new Error(errorMessage);
            }
            showMessage(
                "Leave applied successfully! Your request is pending manager approval.",
                "success"
            );
            leaveForm.reset();
            certificateSection.classList.add("d-none");
            certificateInput.required = false;
        } catch (error) {
            showMessage(
                error.message || "Unable to submit leave request.",
                "danger"
            );
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = "Apply Leave";
        }
    });
    function showMessage(text, type) {

        message.className = "mt-3 alert alert-" + type;
        message.textContent = text;
    }
});
// Load Leave Types from Database
async function loadLeaveTypes() {
    const dropdown = document.getElementById("leaveTypeId");
    try {
        const response = await fetch("/api/leave-types");
        if (!response.ok) {
            throw new Error("Unable to load Leave Types.");
        }
        const leaveTypes = await response.json();
        dropdown.innerHTML =
            '<option value="">-- Select Leave Type --</option>';
        leaveTypes.forEach(type => {
            const option = document.createElement("option");
            option.value = type.id;
            option.textContent = type.leaveName;
            dropdown.appendChild(option);
        });
    } catch (error) {
        alert(error.message);
    }
}