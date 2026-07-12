document.addEventListener("DOMContentLoaded", loadEmployees);

async function loadEmployees() {

    const response = await fetch("/api/employees");

    const employees = await response.json();

    const table = document.getElementById("employeeTable");

    table.innerHTML = "";

    employees.forEach(employee => {

        table.innerHTML += `
            <tr>
                <td>${employee.employeeCode}</td>
                <td>${employee.firstName} ${employee.lastName}</td>
                <td>${employee.email}</td>
                <td>${employee.designation}</td>
                <td>${employee.department ? employee.department.departmentName : ""}</td>
            </tr>
        `;

    });

}