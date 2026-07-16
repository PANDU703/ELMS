let modal;
let selectedLeaveId = null;
let selectedAction = null;

document.addEventListener("DOMContentLoaded", () => {

    modal = new bootstrap.Modal(document.getElementById("remarksModal"));

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

    table.innerHTML = "";

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

        table.innerHTML += `
            <tr>

                <td>${leave.id}</td>

                <td>
                    ${leave.employee.firstName}
                    ${leave.employee.lastName}
                </td>

                <td>${leave.leaveType.leaveName}</td>

                <td>${leave.startDate}</td>

                <td>${leave.endDate}</td>

                <td>${leave.totalDays}</td>

                <td>${statusBadge}</td>

                <td>${actionButtons}</td>

            </tr>
        `;

    });

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

        });

    if (response.ok) {

        modal.hide();

        if (selectedAction === "approve") {

            alert("✅ Leave Approved Successfully");

        } else {

            alert("❌ Leave Rejected Successfully");

        }

        loadLeaves();

    } else {

        alert("Operation Failed.");

    }

}