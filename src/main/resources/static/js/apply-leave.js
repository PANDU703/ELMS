document.addEventListener("DOMContentLoaded", () => {

    const leaveForm = document.getElementById("leaveForm");

    leaveForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const employeeId = document.getElementById("employeeId").value;
        const leaveTypeId = document.getElementById("leaveTypeId").value;
        const startDate = document.getElementById("startDate").value;
        const endDate = document.getElementById("endDate").value;
        const reason = document.getElementById("reason").value;

        // Calculate total days
        const start = new Date(startDate);
        const end = new Date(endDate);

        const totalDays =
            Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;

        const leaveRequest = {

            employee: {
                id: employeeId
            },

            leaveType: {
                id: leaveTypeId
            },

            startDate: startDate,
            endDate: endDate,
            totalDays: totalDays,
            reason: reason

        };

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

        } else {

            alert("❌ Failed to Apply Leave.");

        }

    });

});