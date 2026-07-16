document.addEventListener("DOMContentLoaded", () => {

    // Load all leave types into the dropdown
    loadLeaveTypes();

    const leaveForm = document.getElementById("leaveForm");

    leaveForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        // Get logged-in employee
        const employeeResponse = await fetch("/api/employees/me");

        if (!employeeResponse.ok) {
            alert("Unable to identify logged-in employee.");
            return;
        }

        const employee = await employeeResponse.json();

        // Get form values
        const leaveTypeId = document.getElementById("leaveTypeId").value;
        const startDate = document.getElementById("startDate").value;
        const endDate = document.getElementById("endDate").value;
        const reason = document.getElementById("reason").value;

        // Calculate total leave days
        const start = new Date(startDate);
        const end = new Date(endDate);

        const totalDays =
            Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;

        // Create leave request object
        const leaveRequest = {

            employee: {
                id: employee.id
            },

            leaveType: {
                id: leaveTypeId
            },

            startDate: startDate,
            endDate: endDate,
            totalDays: totalDays,
            reason: reason

        };

        // Send request to backend
        const response = await fetch("/api/leaves", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(leaveRequest)

        });

        if (response.ok) {

            alert("✅ Leave Applied Successfully!");

            leaveForm.reset();

            // Reload leave types after reset
            loadLeaveTypes();

        } else {

            alert("❌ Failed to Apply Leave.");

        }

    });

});

// Load Leave Types from Database
async function loadLeaveTypes() {

    const response = await fetch("/api/leave-types");

    if (!response.ok) {
        alert("Unable to load Leave Types.");
        return;
    }

    const leaveTypes = await response.json();

    const dropdown = document.getElementById("leaveTypeId");

    // Remove old options except the first one
    dropdown.innerHTML = '<option value="">-- Select Leave Type --</option>';

    leaveTypes.forEach(type => {

        dropdown.innerHTML += `
            <option value="${type.id}">
                ${type.leaveName}
            </option>
        `;

    });

}