document.addEventListener("DOMContentLoaded", () => {

    console.log("ELMS Pro Loaded Successfully 🚀");

    const loginForm = document.getElementById("loginForm");

    loginForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        const response = await fetch("/api/auth/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                email: email,
                password: password
            })

        });

        if (response.ok) {

            alert("Login Successful ✅");

            window.location.href = "/dashboard";

        } else {

            alert("Invalid Email or Password ❌");

        }

    });

});