document.addEventListener("DOMContentLoaded", () => {

    fetch("/api/dashboard/stats")

        .then(response => response.json())

        .then(data => {

            document.getElementById("totalEmployees").innerText = data.employees;

            document.getElementById("pendingLeaves").innerText = data.pending;

            document.getElementById("approvedLeaves").innerText = data.approved;

            document.getElementById("rejectedLeaves").innerText = data.rejected;

        })

        .catch(error => {

            console.error(error);

        });

});