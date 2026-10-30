
let modal;
let selectedLeaveId = null;
let selectedAction = null;

document.addEventListener("DOMContentLoaded", () => {

    modal = new bootstrap.Modal(
        document.getElementById("remarksModal")
    );

    loadLeaves();

    document.getElementById("confirmAction")
        .addEventListener("click", submitAction);

});

async function loadLeaves() {

    const response = await fetch("/api/leaves");

    if (!response.ok) {
        alert("Unable to load leave requests.");
        return;
    }

    const leaves = await response.json();

    const table = document.getElementById("leaveTable");

    table.replaceChildren();

    leaves.forEach(leave => {

        let statusBadge = "";

        if (leave.status === "PENDING") {
            statusBadge =
                '<span class="badge bg-warning text-dark">Pending</span>';
        } else if (leave.status === "APPROVED") {
            statusBadge =
                '<span class="badge bg-success">Approved</span>';
        } else {
            statusBadge =
                '<span class="badge bg-danger">Rejected</span>';
        }

        // Certificate and screening information
        let certificateContent = "-";

        if (leave.certificateFileName) {

            let screeningHTML = `
                <p class="text-muted mb-1">
                    Screening result unavailable.
                </p>
            `;

            if (leave.screeningResultJson) {

                try {

                    const screening =
                        JSON.parse(leave.screeningResultJson);

                    let checksHTML = "";

                    Object.entries(screening.checks || {})
                        .forEach(([name, passed]) => {

                            checksHTML += `
                                <li>
                                    ${escapeHTML(name.replaceAll("_", " "))}
                                    :
                                    <strong>
                                        ${passed ? "Detected" : "Not detected"}
                                    </strong>
                                </li>
                            `;

                        });

                    screeningHTML = `
                        <p>
                            <strong>Screening:</strong>
                            ${escapeHTML(
                                screening.screening_message ||
                                "Manual review recommended."
                            )}
                        </p>

                        <p>
                            <strong>Authenticity verified:</strong>
                            No
                        </p>

                        <strong>Information checks:</strong>
                        <ul>${checksHTML}</ul>

                        <strong>Extracted text:</strong>

                        <pre class="bg-light border rounded p-2"
                             style="white-space:pre-wrap;overflow-wrap:anywhere;">${escapeHTML(
                                 screening.extracted_text || "No text detected."
                             )}</pre>
                    `;

                } catch (error) {

                    screeningHTML = `
                        <p class="text-danger">
                            Unable to read screening information.
                        </p>
                    `;
                }
            }

            certificateContent = `
                <a href="/api/leaves/${leave.id}/certificate"
                   target="_blank"
                   rel="noopener noreferrer"
                   class="btn btn-primary btn-sm">
                    View Certificate
                </a>

                <details class="mt-2">
                    <summary>AI Screening Details</summary>
                    <div class="mt-2 small">
                        ${screeningHTML}
                    </div>
                </details>
            `;
        }

        // Existing approval and rejection buttons
        let actionButtons = "-";

        if (leave.status === "PENDING") {

            actionButtons = `
                <button class="btn btn-success btn-sm"
                        onclick="openModal(${leave.id}, 'approve')">
                    Approve
                </button>

                <button class="btn btn-danger btn-sm ms-2"
                        onclick="openModal(${leave.id}, 'reject')">
                    Reject
                </button>
            `;
        }

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${leave.id}</td>

            <td>
                ${escapeHTML(leave.employee.firstName)}
                ${escapeHTML(leave.employee.lastName || "")}
            </td>

            <td>${escapeHTML(leave.leaveType.leaveName)}</td>

            <td>${escapeHTML(leave.startDate)}</td>

            <td>${escapeHTML(leave.endDate)}</td>

            <td>${leave.totalDays}</td>

            <td>${statusBadge}</td>

            <td>${certificateContent}</td>

            <td>${actionButtons}</td>
        `;

        table.appendChild(row);

    });

}

// Escape text before inserting it into HTML
function escapeHTML(value) {

    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);

}

function openModal(id, action) {

    selectedLeaveId = id;
    selectedAction = action;

    document.getElementById("managerRemarks").value = "";

    if (action === "approve") {

        document.querySelector(".modal-title").textContent =
            "Approve Leave";

        document.getElementById("confirmAction").className =
            "btn btn-success";

        document.getElementById("confirmAction").textContent =
            "Approve";

    } else {

        document.querySelector(".modal-title").textContent =
            "Reject Leave";

        document.getElementById("confirmAction").className =
            "btn btn-danger";

        document.getElementById("confirmAction").textContent =
            "Reject";
    }

    modal.show();
}

async function submitAction() {

    const remarks =
        document.getElementById("managerRemarks").value;

    if (selectedAction === "reject" && remarks.trim() === "") {

        alert("Please enter rejection remarks.");
        return;
    }

    const confirmButton =
        document.getElementById("confirmAction");

    confirmButton.disabled = true;

    try {

        const response = await fetch(
            `/api/leaves/${selectedLeaveId}/${selectedAction}`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    managerRemarks: remarks
                })
            }
        );

        if (response.ok) {

            modal.hide();

            alert(
                selectedAction === "approve"
                    ? "Leave Approved Successfully"
                    : "Leave Rejected Successfully"
            );

            await loadLeaves();

        } else {

            alert("Operation Failed.");

        }

    } catch (error) {

        alert("Unable to contact the server.");

    } finally {

        confirmButton.disabled = false;

    }
}
