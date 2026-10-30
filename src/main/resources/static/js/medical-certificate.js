
document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("certificateForm");
    const input = document.getElementById("certificateFile");
    const button = document.getElementById("screenButton");
    const status = document.getElementById("status");
    const results = document.getElementById("results");
    const summary = document.getElementById("summary");
    const checklist = document.getElementById("checklist");
    const extractedText = document.getElementById("extractedText");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        results.classList.add("d-none");

        const file = input.files[0];

        if (!file) {
            status.className = "mt-4 alert alert-danger";
            status.textContent = "Please choose an image.";
            return;
        }

        if (file.size > 8 * 1024 * 1024) {
            status.className = "mt-4 alert alert-danger";
            status.textContent = "The image must be 8 MB or smaller.";
            return;
        }

        const data = new FormData();
        data.append("file", file);

        button.disabled = true;
        status.className = "mt-4 alert alert-info";
        status.textContent = "Screening image. Please wait...";

        try {
            const response = await fetch(
                "/api/medical-certificates/screen",
                { method: "POST", body: data }
            );

            const payload = await response.json();

            if (!response.ok) {
                throw new Error(
                    payload.error || payload.detail || "Screening failed."
                );
            }

            status.className = "mt-4 alert alert-success";
            status.textContent = "Text extraction completed.";

            summary.className = "alert alert-warning";
            summary.textContent =
                (payload.screening_message ||
                 "Review the extracted information manually.") +
                " Authenticity has not been verified.";

            checklist.replaceChildren();

            Object.entries(payload.checks || {}).forEach(([name, passed]) => {
                const item = document.createElement("li");
                item.className =
                    "list-group-item d-flex justify-content-between";

                const label = document.createElement("span");
                label.textContent = name.replaceAll("_", " ");

                const result = document.createElement("span");
                result.className = passed
                    ? "badge text-bg-success"
                    : "badge text-bg-warning";
                result.textContent = passed ? "Detected" : "Not detected";

                item.append(label, result);
                checklist.appendChild(item);
            });

            extractedText.textContent =
                payload.extracted_text || "(No text detected.)";

            results.classList.remove("d-none");
        } catch (error) {
            status.className = "mt-4 alert alert-danger";
            status.textContent =
                error.message || "Could not contact the screening service.";
        } finally {
            button.disabled = false;
        }
    });
});
