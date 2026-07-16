document.addEventListener("DOMContentLoaded", loadLeaves);

async function loadLeaves() {

    const response = await fetch("/api/leaves/my");
    const leaves = await response.json();

    const table = document.getElementById("leaveTable");
    table.innerHTML = "";

    leaves.forEach(leave => {

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
        </tr>
        `;

    });

}