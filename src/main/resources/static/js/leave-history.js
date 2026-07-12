document.addEventListener("DOMContentLoaded", loadLeaves);

async function loadLeaves() {

    const response = await fetch("/api/leaves");
    const leaves = await response.json();

    const table = document.getElementById("leaveTable");
    table.innerHTML = "";

    leaves.forEach(leave => {

        let actionButtons = "";

        if (leave.status === "PENDING") {

            actionButtons = `
                <button class="btn btn-success btn-sm"
                        onclick="approveLeave(${leave.id})">
                    Approve
                </button>

                <button class="btn btn-danger btn-sm ms-2"
                        onclick="rejectLeave(${leave.id})">
                    Reject
                </button>
            `;

        } else {

            actionButtons = "-";

        }

        table.innerHTML += `
        <tr>

            <td>${leave.id}</td>

            <td>${leave.employee.firstName} ${leave.employee.lastName}</td>

            <td>${leave.leaveType.leaveName}</td>

            <td>${leave.startDate}</td>

            <td>${leave.endDate}</td>

            <td>${leave.totalDays}</td>

            <td>
                ${leave.status==="PENDING"
                ?'<span class="badge bg-warning text-dark">Pending</span>'
                :leave.status==="APPROVED"
                ?'<span class="badge bg-success">Approved</span>'
                :'<span class="badge bg-danger">Rejected</span>'
                }
            </td>

            <td>${actionButtons}</td>

        </tr>
        `;

    });

}

async function approveLeave(id) {

    const response = await fetch(`/api/leaves/${id}/approve`, {
        method: "PUT"
    });

    if(response.ok){

        alert("✅ Leave Approved");

        loadLeaves();

    }else{

        alert("Failed");

    }

}

async function rejectLeave(id) {

    const response = await fetch(`/api/leaves/${id}/reject`, {
        method: "PUT"
    });

    if(response.ok){

        alert("❌ Leave Rejected");

        loadLeaves();

    }else{

        alert("Failed");

    }

}