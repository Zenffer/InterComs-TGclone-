document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("login-form");
    const signupForm = document.getElementById("signup-form");
    const showSignupLink = document.getElementById("show-signup");
    const showLoginLink = document.getElementById("show-login");

    // Show signup form and hide login form
    showSignupLink.addEventListener("click", (e) => {
        e.preventDefault();
        loginForm.classList.add("hidden");
        signupForm.classList.remove("hidden");
    });

    // Show login form and hide signup form
    showLoginLink.addEventListener("click", (e) => {
        e.preventDefault();
        signupForm.classList.add("hidden");
        loginForm.classList.remove("hidden");
    });

    // Handle login form submission
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const email = document.getElementById("login-email").value.trim();
        const password = document.getElementById("login-password").value.trim();

        // Basic validation
        if (!email || !password) {
            alert("Please fill in all fields.");
            return;
        }
        if (password.length < 6) {
            alert("Password must be at least 6 characters long.");
            return;
        }

        try {
            const response = await fetch("/backend/auth/login.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
            });

            const result = await response.json();
            if (response.ok) {
                alert("Login successful!");
                window.location.href = "chat.html";
            } else {
                alert(result.message || "Login failed. Please try again.");
            }
        } catch (error) {
            console.error("Error during login:", error);
            alert("An error occurred. Please try again later.");
        }
    });

    // Handle signup form submission
    signupForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const name = document.getElementById("signup-name").value.trim();
        const email = document.getElementById("signup-email").value.trim();
        const password = document.getElementById("signup-password").value.trim();

        // Basic validation
        if (!email || !password) {
            alert("Please fill in all required fields.");
            return;
        }
        if (password.length < 6) {
            alert("Password must be at least 6 characters long.");
            return;
        }

        try {
            const response = await fetch("/backend/auth/signup.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, email, password }),
            });

            const result = await response.json();
            if (response.ok) {
                alert("Signup successful!");
                window.location.href = "chat.html";
            } else {
                alert(result.message || "Signup failed. Please try again.");
            }
        } catch (error) {
            console.error("Error during signup:", error);
            alert("An error occurred. Please try again later.");
        }
    });
});