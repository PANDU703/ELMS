document.addEventListener("DOMContentLoaded", () => {

    // Load Departments into Dropdown
    loadDepartments();

    const signupForm = document.getElementById("signupForm");

    signupForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const employee = {

            employeeCode: document.getElementById("employeeCode").value,

            firstName: document.getElementById("firstName").value,

            lastName: document.getElementById("lastName").value,

            email: document.getElementById("email").value,

            password: document.getElementById("password").value,

            phone: document.getElementById("phone").value,

            designation: document.getElementById("designation").value,

            joiningDate: document.getElementById("joiningDate").value,

            role: "EMPLOYEE",

            department: {
                id: document.getElementById("departmentId").value
            }

        };

        try {

            const response = await fetch("/api/employees", {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(employee)

            });

            if (response.ok) {

                alert("🎉 Registration Successful!");

                window.location.href = "/login";

            } else {

                const error = await response.text();

                alert("Registration Failed\n\n" + error);

            }


        } catch (error) {

            console.error(error);

            alert("Unable to connect to server.");

        }

    });

});

// Load Departments from Database
async function loadDepartments() {

    try {

        const response = await fetch("/api/departments");

        if (!response.ok) {
            throw new Error("Unable to load departments.");
        }

        const departments = await response.json();

        const dropdown = document.getElementById("departmentId");

        dropdown.innerHTML = '<option value="">-- Select Department --</option>';

        departments.forEach(department => {

            dropdown.innerHTML += `
                <option value="${department.id}">
                    ${department.departmentCode} - ${department.departmentName}
                </option>
            `;

        });

    } catch (error) {

        console.error(error);

        alert("Unable to load departments.");

    }

}