document.addEventListener("DOMContentLoaded", loadProfile);

async function loadProfile() {

    // Temporary: displaying Employee ID 3.
    // After Spring Security, this will automatically use the logged-in user.
    const employeeId = 3;

    const response = await fetch(`/api/employees/${employeeId}`);

    if (!response.ok) {
        alert("Unable to load profile.");
        return;
    }

    const employee = await response.json();

    document.getElementById("employeeCode").textContent = employee.employeeCode;
    document.getElementById("fullName").textContent =
        employee.firstName + " " + employee.lastName;
    document.getElementById("email").textContent = employee.email;
    document.getElementById("phone").textContent = employee.phone;
    document.getElementById("designation").textContent = employee.designation;
    document.getElementById("joiningDate").textContent = employee.joiningDate;
    document.getElementById("role").textContent = employee.role;

    if (employee.department) {
        document.getElementById("department").textContent =
            employee.department.departmentName;
    } else {
        document.getElementById("department").textContent = "-";
    }
}